## Student Details

| | |
|---|---|
| **Name** | Prabhat Kumar |
| **USN** | 25MCAR0110 |
| **Class** | MCA (General) |

## Technologies

- Android Studio
- Java
- XML
- Android SDK
- Material Components

## Experiments

### Experiment 4 — Linking Activities Using Intents

Implemented:

- Login Activity
- Signup Activity
- Dashboard Activity
- Explicit Intent for Activity navigation
- Implicit Intent for:
   - Website
   - Phone Dialer
   - Email
   - Maps

#### Screenshot

![Experiment 4.1 - Implicit Intents](screenshots/exp4.1-intent.png)
![Experiment 4.2 - Implicit Intents](screenshots/exp4.2-intent.png)

---

### Experiment 5 — Android Notification

Implemented:

- Notification Channel
- Notification permission
- Android notification
- Heads-up / pop-up notification
- Notification panel notification

#### Screenshot

![Experiment 5 - Notification](screenshots/exp5.1-notification.png)
![Experiment 5 - Notification](screenshots/exp5.2-notification.png)
---

### Experiment 6 — Android Basic Views

Implemented:

- TextView
- Radio
- Button


#### Screenshot

![Experiment 6 - Notification](screenshots/6.1.jpeg)
![Experiment 6 - Notification](screenshots/6.2.jpeg)
---

### Experiment 7 — Adaptive UI (ListView and ImageView)

Implemented:

- Custom `ArrayAdapter` (`AdaptiveListAdapter`) for `ListView`
- Custom row item layout (`list_item_adaptive.xml`) with `ImageView` and `TextView`s
- Adaptive detail preview card updating dynamically on list item selection
- Interactive item selection handling and Toast notifications
---

#### Screenshot

![Experiment 7 - Notification](screenshots/exp7.png)
---

### Experiment 8 — Menus and WebView

Implemented:

- Android Options Menu (`res/menu/menu_web_view.xml`) & Popup/Context Menu
- Integrated `WebView` with JavaScript enabled and zoom controls
- In-app URL navigation handling using `WebViewClient` and `WebChromeClient`
- Interactive URL address bar, search action, and horizontal progress bar
- Bottom navigation bar controls (Back, Forward, Refresh, Home)
- System Back Press dispatcher handling for WebView navigation history
---

#### Screenshot

![Experiment 8 - Notification](screenshots/exp8.1.png)
![Experiment 8 - Notification](screenshots/exp8.2.png)
---

## Project Structure

```text
AndroidExperiments
├── app
├── screenshots
│   ├── exp4-intent.png
│   └── exp5-notification.png
├── build.gradle.kts
├── settings.gradle.kts
└── README.md