# IBrain Legacy Flash Light

A lightweight flashlight application designed to support old Android devices with reliable camera flash functionality.

## Description

IBrain Legacy Flash Light is a free Android application that provides a simple and efficient way to use your device's camera flash as a flashlight. The app is optimized to work seamlessly on older Android devices, featuring dual compatibility for both modern and legacy Android versions.

## Features

- **Simple Toggle Interface**: Easy-to-use switch to turn the flashlight on and off
- **Legacy Device Support**: Compatible with Android 5.0 (API 21) and above
- **Dual Camera API Support**: Automatically uses the appropriate camera API based on Android version
  - Android 6.0 and above: Uses modern Camera2 API with CameraManager
  - Below Android 6.0: Uses legacy Camera API for backward compatibility
- **Permission Management**: Handles camera permissions with user-friendly dialogs
- **Optimized Performance**: Minimal resource usage for older devices
- **Help Dialog**: Built-in help information for users

## Technical Details

### Android Requirements
- **Minimum SDK**: Android 5.0 (API 21)
- **Target SDK**: Android 12 (API 33)
- **Language**: Kotlin

### Key Dependencies
- AndroidX AppCompat
- AndroidX ConstraintLayout
- Material Design Components
- AndroidX Core KTX

### Hardware Requirements
- Camera with flash capability

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/nguyenlamminhhoa/IBrain-Legacy-Flash-Light.git
   ```
2. Open the project in Android Studio
3. Build and run the application on your Android device

## Permissions
The application requires the following permissions:
- **CAMERA**: To access the device's camera flash

## Build Information
- **Build System**: Gradle
- **Kotlin Version**: 1.8.20
- **Gradle Plugin**: 8.0.2
- **Compile SDK**: 33

## License
This project is licensed under the GNU Affero General Public License v3.0 - see the LICENSE file for details.

## Author
nguyenlamminhhoa

## Contributing
Contributions are welcome! Feel free to fork the repository and submit pull requests.

## Support
For issues, bug reports, or feature requests, please open an issue on the GitHub repository.
