# 📵 Call Blocker — Pattern-Based Call Blocking for Android

Block spam calls using patterns, prefixes, regex — not just one number at a time.

---

## ✅ What This App Does

| Feature | Details |
|---|---|
| **Block by prefix** | Block all calls starting with `+91140` (TRAI telemarketers), `1800`, `0120`, etc. |
| **Block exact number** | Block one specific number forever |
| **Block by pattern** | Contains, ends-with, or full regex |
| **See blocked calls** | A log shows every call that was silently blocked |
| **Toggle rules** | Turn a rule on/off without deleting it |
| **Works offline** | No account. No data sent anywhere. |

---

## 📦 How to Get the APK on Your Phone

### STEP 1 — Create a free GitHub account
1. Go to **https://github.com**
2. Click **Sign up** and create a free account (just needs an email)

### STEP 2 — Create a new repository
1. After signing in, click the **+** button in the top-right
2. Click **New repository**
3. Name it: `CallBlocker`
4. Make sure **Public** is selected
5. Click **Create repository**

### STEP 3 — Upload the project files
1. On the new repository page, click **uploading an existing file** (there's a link in the page)
2. **Drag the entire `CallBlocker` folder** from your computer onto the page
   - Or click "choose your files" and select all files inside the CallBlocker folder
3. Scroll down and click **Commit changes**

### STEP 4 — Watch it build (takes about 5 minutes)
1. Click the **Actions** tab at the top of your repository page
2. You'll see "Build CallBlocker APK" running with a yellow spinner ⏳
3. Wait for it to turn green ✅

### STEP 5 — Download your APK
1. Click on the completed build (the green checkmark row)
2. Scroll to the bottom — you'll see **Artifacts**
3. Click **CallBlocker-debug-apk** to download it
4. You'll get a ZIP file — open it, and inside is `app-debug.apk`

### STEP 6 — Install on your Android phone

> **You only do steps A and B once. Never again.**

**A. Allow installation from unknown sources:**
- Open your phone's **Settings**
- Go to **Apps** (or Application Manager)
- Tap the three-dot menu → **Special app access** → **Install unknown apps**
- Find your **Files** or **Downloads** app → toggle **Allow from this source**

**B. Transfer and install the APK:**
- Send the `app-debug.apk` file to your phone (WhatsApp, email, USB cable, Google Drive — any way)
- Open it on your phone using the Files app
- Tap **Install**

**C. Grant the Caller ID permission (the one-time setup inside the app):**
- Open **Call Blocker** on your phone
- You'll see a setup screen — tap **Grant Caller ID Permission**
- A dialog pops up — tap **Allow**
- Done! The app is now blocking calls.

---

## 🛠️ How to Use the App

### Adding a blocking rule
1. Open the app
2. Tap the **+** button
3. Choose your rule type:
   - **Starts with** — best for blocking whole telemarketer ranges (e.g. `140`)
   - **Exact** — for one specific number
   - **Contains / Ends with** — for patterns in the middle or end
   - **Regex** — advanced pattern matching
4. Type the pattern
5. Optionally add a label like "TRAI Spam" so you remember why you added it
6. Tap **Add Rule**

### Useful patterns for India

| Pattern | Type | Blocks |
|---|---|---|
| `+91140` | Starts with | All TRAI commercial callers (140xxx) |
| `1800` | Starts with | Toll-free spam calls |
| `0120` | Starts with | Noida-based call centres |
| `000000` | Contains | Fake/spoofed numbers with repeated zeros |
| `+919999999999` | Exact | One specific number |

### Seeing blocked calls
- Tap the **Blocked Log** tab at the bottom
- See every call that was silently blocked, which rule matched, and when

### Toggling a rule
- Use the toggle switch on any rule to temporarily disable it without deleting

---

## ❓ Troubleshooting

**The app isn't blocking calls**
- Check that you granted the "Caller ID" permission (the setup screen)
- Make sure you have at least one rule added and it's turned on

**I accidentally blocked a number I want**
- Go to the Rules tab and delete or toggle off the rule that matched it
- Some phones also let you add individual numbers to a whitelist — this feature is coming in v2

**The build failed on GitHub**
- Click on the failed build and read the error — usually it's a typo in the code
- You can re-run the build by clicking "Re-run all jobs"

---

## 🔒 Privacy

- This app has **no internet permission** — it cannot phone home
- All rules and blocked call logs stay only on your device
- No account, no subscription, no ads
