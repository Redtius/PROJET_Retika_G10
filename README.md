# Retika: Social Media Application

Retika is a feature-rich social media application that allows users to share, explore, and interact with content. It includes functionalities such as user authentication, profile management, post creation, and applying filters to images. The app is built using Firebase for backend services and Android for the frontend.

## Features

### User Authentication
- **Login and Registration**: Users can create accounts and log in securely using Firebase Authentication.
- **Password Management**: Allows users to change their passwords or recover them if forgotten.

### Profile Management
- **Profile View**: Users can view their profile, including followers, total likes, and their posts.
- **Followers and Likes**: Displays the count of followers and the total likes across posts.
- **Profile Picture Upload**: Users can upload and update their profile picture.

### Posts
- **Post Creation**: Users can create posts by uploading images and adding captions.
- **Image Editing**:
  - Apply filters such as Sepia, Grayscale, and Brightness.
  - Crop images to a desired size.
- **Post Feed**:
  - Displays a list of posts in a RecyclerView.
  - Supports infinite scrolling for seamless pagination.

### Real-Time Updates
- **Firestore Integration**: Real-time updates for posts, likes, followers, and user information.

### Tools and Filters
- **GPUImage Filters**: Enables advanced image filtering capabilities.
- **Dynamic Bitmap Manipulation**: Allows cropping and applying filters to images before posting.

## Tech Stack

### Frontend
- **Android**: Built using Java and XML.
- **Material Design**: For a modern and intuitive user interface.

### Backend
- **Firebase**:
  - Authentication: For user login and registration.
  - Firestore: For storing user and post data in real-time.
  - Storage: For saving and retrieving images.

### Libraries
- **Glide**: For image loading and caching.
- **GPUImage**: For applying filters to images.
- **AndroidX**: Modern Android components.

## Project Structure

### Key Modules
1. **Authentication**: Handles user login, registration, and password management.
2. **Home Feed**:
   - Displays a list of posts from all users.
   - Supports infinite scrolling.
3. **Profile Management**:
   - Displays user-specific data such as followers, likes, and posts.
   - Provides functionality to upload profile pictures.
4. **Post Editor**:
   - Allows users to create posts by uploading images.
   - Provides image editing tools like cropping and filters.

### File Structure
```
app/
├── adapters/
│   ├── PostAdapter.java
│   ├── CommentAdapter.java
├── models/
│   ├── User.java
│   ├── Post.java
│   ├── Comment.java
├── ui/
│   ├── home/HomeFragment.java
│   ├── other/ProfileFragment.java
│   ├── other/PostEditorFragment.java
├── res/
│   ├── layout/ (XML layouts)
│   ├── drawable/ (Icons and images)
│   ├── values/ (Colors, strings, etc.)
```

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/retika.git
   ```
2. Open the project in Android Studio.
3. Set up Firebase:
   - Create a Firebase project.
   - Add the `google-services.json` file to the `app/` directory.
   - Enable Firebase Authentication, Firestore, and Storage.
4. Sync the project with Gradle files.
5. Run the app on an emulator or physical device.

## Usage

### Post Creation
1. Tap the floating action button (FAB) to add an image.
2. Choose an image from the gallery or take a new photo.
3. Apply filters or crop the image.
4. Add a caption and post.

### Profile
1. View followers and likes count.
2. Upload or update your profile picture.

### Home Feed
1. Scroll through posts.
2. Like posts and view comments.

## Future Enhancements
- Implement push notifications for likes and comments.
- Add user-to-user messaging.
- Include hashtags and search functionality.
- Enhance image editing with more filters and tools.

## License
This project is licensed under the [MIT License](LICENSE).

---

Thank you for exploring Retika! Feel free to contribute or report issues in the repository.

