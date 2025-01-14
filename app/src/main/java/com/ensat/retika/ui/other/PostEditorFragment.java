/**
 * Fragment class for the Post Editor screen.
 * Allows users to upload, edit, and apply filters to images before posting.
 */
package com.ensat.retika.ui.other;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ensat.retika.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;

public class PostEditorFragment extends Fragment {

    private ImageView imagePreview;
    private TextInputEditText inputCaption;
    private MaterialButton buttonPost;
    private FloatingActionButton fabAddImage;
    private MaterialButton buttonCrop;
    private MaterialButton buttonFilter;

    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private GPUImage gpuImage;
    private Bitmap originalBitmap;
    private Bitmap croppedBitmap;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        imageUri = selectedImageUri;
                        loadImageIntoPreview(selectedImageUri);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    if (imageUri != null) {
                        loadImageIntoPreview(imageUri);
                    }
                }
            }
    );

    /**
     * Called when the fragment is created.
     * Initializes Firebase and GPUImage instances.
     *
     * @param savedInstanceState Saved state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        gpuImage = new GPUImage(requireContext());
    }

    /**
     * Creates and returns the view hierarchy for the fragment.
     *
     * @param inflater           LayoutInflater to inflate views in the fragment.
     * @param container          Parent view that the fragment's UI should attach to.
     * @param savedInstanceState Saved state of the fragment.
     * @return Root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_post_editor, container, false);

        imagePreview = root.findViewById(R.id.image_preview);
        inputCaption = root.findViewById(R.id.input_caption);
        buttonPost = root.findViewById(R.id.button_post);
        fabAddImage = root.findViewById(R.id.fab_add_image);
        buttonCrop = root.findViewById(R.id.button_crop);
        buttonFilter = root.findViewById(R.id.button_filter);

        fabAddImage.setOnClickListener(v -> showImagePickerDialog());
        buttonPost.setOnClickListener(v -> createPost());
        buttonCrop.setOnClickListener(v -> cropImage());
        buttonFilter.setOnClickListener(v -> showFilterDialog());

        return root;
    }

    /**
     * Displays a dialog to select an image source (camera or gallery).
     */
    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Add Photo");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                openCamera();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    /**
     * Opens the gallery to select an image.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    /**
     * Opens the camera to capture an image.
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = new File(requireContext().getExternalCacheDir(), "temp_photo.jpg");
        imageUri = Uri.fromFile(photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(intent);
    }

    /**
     * Loads the selected image into the preview.
     *
     * @param imageUri URI of the selected image.
     */
    private void loadImageIntoPreview(Uri imageUri) {
        try {
            originalBitmap = BitmapFactory.decodeStream(requireContext().getContentResolver().openInputStream(imageUri));
            imagePreview.setImageBitmap(originalBitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Crops the selected image to a square format.
     */
    private void cropImage() {
        if (originalBitmap == null) {
            Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        int size = Math.min(width, height);

        int x = (width - size) / 2;
        int y = (height - size) / 2;

        croppedBitmap = Bitmap.createBitmap(originalBitmap, x, y, size, size);
        imagePreview.setImageBitmap(croppedBitmap);
    }

    /**
     * Displays a dialog to apply filters to the image.
     */
    private void showFilterDialog() {
        if (originalBitmap == null) {
            Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] filters = {"Sepia", "Grayscale", "Brightness"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Choose a Filter");
        builder.setItems(filters, (dialog, which) -> {
            switch (which) {
                case 0:
                    applyFilter(new GPUImageSepiaToneFilter());
                    break;
                case 1:
                    applyFilter(new GPUImageGrayscaleFilter());
                    break;
                case 2:
                    applyFilter(new GPUImageBrightnessFilter(0.5f));
                    break;
            }
        });
        builder.show();
    }

    /**
     * Applies the selected filter to the image.
     *
     * @param filter GPUImageFilter to apply.
     */
    private void applyFilter(GPUImageFilter filter) {
        gpuImage.setImage(croppedBitmap != null ? croppedBitmap : originalBitmap);
        gpuImage.setFilter(filter);
        Bitmap filteredBitmap = gpuImage.getBitmapWithFilterApplied();
        imagePreview.setImageBitmap(filteredBitmap);
    }

    /**
     * Creates a post with the selected image and caption.
     */
    private void createPost() {
        String caption = inputCaption.getText().toString().trim();

        if (originalBitmap == null) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(caption)) {
            Toast.makeText(requireContext(), "Please enter a caption", Toast.LENGTH_SHORT).show();
            return;
        }

        saveFinalImage();
    }

    /**
     * Saves the final image (cropped and filtered) locally.
     */
    private void saveFinalImage() {
        Bitmap finalBitmap = gpuImage.getBitmapWithFilterApplied();
        if (finalBitmap == null) {
            Toast.makeText(requireContext(), "Failed to apply filter", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(requireContext().getCacheDir(), "final_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            Uri finalImageUri = Uri.fromFile(file);
            uploadImageToFirebase(finalImageUri, inputCaption.getText().toString().trim());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Failed to save final image", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Uploads the image to Firebase Storage and saves the post to Firestore.
     *
     * @param imageUri URI of the image to upload.
     * @param caption  Caption for the post.
     */
    private void uploadImageToFirebase(Uri imageUri, String caption) {
        String userId = mAuth.getCurrentUser().getUid();
        String fileName = "posts/" + userId + "/" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storage.getReference().child(fileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            HashMap<String, Object> post = new HashMap<>();
                            post.put("userId", userId);
                            post.put("imageUrl", uri.toString());
                            post.put("caption", caption);
                            post.put("timestamp", System.currentTimeMillis());

                            db.collection("posts").add(post)
                                    .addOnSuccessListener(documentReference ->
                                            Toast.makeText(requireContext(), "Post created successfully", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(requireContext(), "Failed to create post", Toast.LENGTH_SHORT).show()
                                    );
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show()
                );
    }
}
