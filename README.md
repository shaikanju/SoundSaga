Here’s a suggested `README.md` file and project description for your **SoundSaga AudioBook Player** (Assignment 3: CSC 392/492 – Mobile Application Development II):

---

### 📖 SoundSaga - AudioBook Player

**SoundSaga** is a feature-rich audiobook player Android app that delivers an engaging listening experience through intuitive design, real-time audio controls, and seamless activity transitions. Built as part of CSC 392/492: Mobile Applications Development II, the app uses advanced Android components such as `MediaPlayer`, `ViewPager2`, `Volley`, and `SplashScreen API`.

---

### 📱 Features

* **Splash Screen**: Introduces the app with a branded animated splash.
* **MainActivity**:

  * Displays a responsive grid of audiobooks using `GridLayoutManager`.
  * Tapping an item plays the audiobook in a new activity.
  * Long press reveals detailed book info (title, author, date, language, duration).
* **AudioBookActivity**:

  * Starts playback on launch.
  * Swipe or tap to navigate between chapters.
  * Seekbar with time display and speed control popup menu (0.75x to 2.0x).
  * Playback speed persists between sessions.
  * 15-second rewind and forward buttons with play/pause control.
  * Saves progress on exit.
* **MyBooksActivity**:

  * Shows in-progress books sorted by most recent.
  * Allows resume from last listening point.
  * Long press to delete progress with confirmation dialog.
* **Landscape support**: Responsive UI with 2-column and 4-column layouts.

---

### 🌐 Data Source

Audiobooks and chapter information are fetched dynamically from:

```
https://christopherhield.com/ABooks/abook_contents.json
```

Example JSON includes:

```json
{
  "title": "Moby Dick, or the Whale",
  "author": "Melville, Herman",
  "date": "1851",
  "language": "English",
  "duration": "24h 37m 50s",
  "image": "...",
  "contents": [
    {
      "number": 1,
      "title": "Etymology and Extracts",
      "url": "..."
    }
  ]
}
```

---

### 🛠 Technologies Used

* **Java**
* **Volley** – for JSON data parsing
* **MediaPlayer** – for streaming and controlling playback
* **ViewPager2** – for chapter swiping
* **Marquee Text** – for dynamic scrolling titles
* **PopupMenu** – for playback speed options
* **AlertDialog** – for deletion and empty state prompts

---

### 📂 Project Structure

* `MainActivity.java`: Audiobook listing and splash screen
* `AudioBookActivity.java`: Playback interface
* `MyBooksActivity.java`: Displays ongoing audiobooks
* `audiobook_contents.json`: Remote data
* `res/layout`: XML layout files for portrait and landscape
* `res/drawable`: Book cover images and icons

---

### 🚀 How to Run

1. Clone or download the repository.
2. Open in **Android Studio**.
3. Ensure internet connection is active (for Volley to load JSON and stream audio).
4. Run on an emulator or Android device (API level 24+ recommended).

---
### 📸 Screenshots
![image](https://github.com/user-attachments/assets/82e69578-eb09-4df3-b1b6-73d07be472bf)
![image](https://github.com/user-attachments/assets/3e062f35-f1e6-4cb4-ba4f-9fb3cb5853ae)
![image](https://github.com/user-attachments/assets/1c1d8407-fdb6-47be-b2a8-8b2f9f5dd979)
![image](https://github.com/user-attachments/assets/d5f3eaa2-70ce-407c-886b-f01e1203d218)
![image](https://github.com/user-attachments/assets/bd30f0c5-6cec-4085-8233-48ad95dd61fc)
![image](https://github.com/user-attachments/assets/1eedb171-ef00-4625-8bd4-235fd5d6b4b7)
![image](https://github.com/user-attachments/assets/5bff7727-c375-484e-b950-3feeb4b14690)
![image](https://github.com/user-attachments/assets/0236551d-f5de-4311-a089-9bf3b6aa70a7)
![image](https://github.com/user-attachments/assets/3cb73376-f599-4ad5-9b7b-0d95a216e774)
![image](https://github.com/user-attachments/assets/efd39f0b-19dd-4df8-ba59-8ea9ce69d349)
![image](https://github.com/user-attachments/assets/69ace153-0f50-40fd-a17b-b6467fba8a27)
![image](https://github.com/user-attachments/assets/a00269d6-f332-43ef-8ce9-c6a6fb4d1f84)
![image](https://github.com/user-attachments/assets/a85a729c-fed3-4845-9886-285b70d39b62)
### 👩‍💻 Author

**Anju Shaik**
Graduate Student, Computer Science
📫 [Portfolio](https://www.shaikanju.com)
