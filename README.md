# 🚗 Car Import Ida (CarSellingShop)

Car Import Ida is an Android application that simulates a car selling and ordering platform, built for the Android course project. It allows users to browse cars, check details, place orders, and manage their profile. Admins can add, edit, and delete car listings directly from the app.

## ✨ Features

### 👥 Authentication
- Sign Up / Sign In
- Forgot Password (reset via Firebase)
- Validation (password must contain number & special character)
- Error handling with proper messages
- 2FA (Bonus, optional) – via email

### 📱 UI & Responsiveness
- Supports multiple screen sizes (phones & tablets)
- Clean material design with CardView & Material Components
- Custom styled buttons (red = delete, green = confirm, etc.)
- Profile avatar with dynamic color background

### 🚀 Functionality
- CRUD operations for cars:
    - Add car
    - Edit car
    - Delete car
    - View details
- Order cars (place order with name, phone, address)
- Notifications (welcome or order notification)
- Profile management:
    - View username & email
    - Edit username (saved to Firestore)
- Admin features:
    - Edit existing cars
    - Delete cars

### 🔒 Security
- Firebase Authentication (secure login)
- Firebase Firestore with rules (per-user data access)
- Passwords stored securely with Firebase
- Sensitive operations checked for signed-in user

## 🛠 Tech Stack
- Language: Java
- IDE: Android Studio
- Database & Auth: Firebase Authentication + Firestore
- UI: Material Design Components, CardView, RecyclerView
- Version Control: Git & GitHub

## 📂 Project Structure

app/ └── java/com/example/carsellingshop/ ├── Activities/     
# All Activities (Login, Signup, Main, Profile, Admin, etc.) ├── Adapters/       
# RecyclerView Adapters ├── Models/         
# Car, Order, User models ├── Services/       
# OrderService, CarService (manages Firestore access) └── ...

## ⚙ Setup Instructions
1. Clone the repository:
   ```bash
   git clone https://github.com/ReartKrasniqi20/CarSellingShop.git
   cd CarSellingShop

2. Open in Android Studio


3. Connect Firebase:

Create a Firebase project in Firebase Console

Add an Android app with your package name

Download the google-services.json file

Place it inside:

app/google-services.json



4. Sync Gradle


5. Run on emulator or physical device



🔑 Notes

Firebase: The app uses Firebase for auth & DB. You must configure your own Firebase project to run it.

Security Rules: Firestore rules should be configured to protect user data.

Admin Access: Admin functionality is based on user role (userType = "admin").


📜 License

This project is licensed under the MIT License.

👨‍💻 Authors

Reart Krasniqi

University of Pristina – Faculty of Electrical and Computer Engineering
