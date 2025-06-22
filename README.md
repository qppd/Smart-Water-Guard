<h1>💧 Smart Water Guard</h1>

<p><strong>Smart Water Guard</strong> is an Android-based IoT application developed by <strong>QPPD</strong> to monitor and manage household or commercial water consumption. It calculates usage costs in real time, using local rates and customizable units. Designed for offline use with prototype hardware.</p>

<blockquote><strong>⚠ Note:</strong> This app requires a connected prototype device to function properly.</blockquote>

<h2>📱 Features</h2>
<ul>
  <li>📊 Real-time water consumption tracking</li>
  <li>💸 Conversion to billing cost in PHP (₱)</li>
  <li>⚙️ Custom units (Gallons, mL, Cubic Meters, Cubic Feet, etc.)</li>
  <li>🧾 Adjustable tax, rates, and meter settings</li>
  <li>📴 Works entirely offline or with Firebase for data logging</li>
</ul>

<h2>🚀 Getting Started</h2>

<h3>Requirements</h3>
<ul>
  <li>Android Studio (latest version)</li>
  <li>Java Development Kit (JDK 8+)</li>
  <li>Android device + Smart Water Guard prototype</li>
  <li>Firebase project with <code>google-services.json</code></li>
</ul>

<h3>Installation</h3>
<ol>
  <li>Clone the repository:<br>
    <code>git clone https://github.com/qppd/Smart-Water-Guard.git</code>
  </li>
  <li>Open the project in Android Studio</li>
  <li>Connect your Android device and build the project</li>
</ol>

<h3>🔐 Firebase Setup</h3>
<ol>
  <li>Go to <a href="https://console.firebase.google.com" target="_blank">Firebase Console</a> and create a new project</li>
  <li>Click "Add app" > Choose "Android"</li>
  <li>Enter your package name (e.g. <code>com.qppd.smartwaterguard</code>)</li>
  <li>Download the <strong>google-services.json</strong> file</li>
  <li>Place it inside the <code>app/</code> folder of your Android project</li>
  <li>Ensure Firebase is initialized in your <code>build.gradle</code> files</li>
</ol>

<h2>📂 Directory Structure</h2>
<pre>
Smart-Water-Guard/
├── app/
│   ├── google-services.json  ← your Firebase config file
│   └── src/main/
│       ├── java/com/qppd/smartwaterguard/
│       │   └── MainActivity.java
│       └── res/
│           ├── layout/
│           └── values/
├── build.gradle
└── README.md
</pre>

<h2>🛠 Tech Stack</h2>
<ul>
  <li><strong>Android</strong> - Java</li>
  <li><strong>Firebase</strong> - Realtime Database</li>
  <li><strong>IoT Prototype Hardware</strong> - Custom-built microcontroller device</li>
</ul>

<h2>📄 License</h2>
<p>This project is developed and maintained by <strong>QPPD</strong>.</p>
<p>© 2025 <strong>QPPD</strong> - All rights reserved.</p>
<p>If you'd like to reuse this work, please contact the author or include a valid open-source license (e.g., MIT, Apache 2.0, or GPL).</p>

<h2>🙋 Author</h2>
<p><strong>Sajed Mendoza / QPPD</strong><br>
GitHub: <a href="https://github.com/qppd">https://github.com/qppd</a><br>
Facebook (Main Page): <a href="https://facebook.com/QUEZONPROVINCEDEVS">facebook.com/QUEZONPROVINCEDEVS</a><br>
Facebook (QPPD): <a href="https://facebook.com/qppd.dev">facebook.com/qppd.dev</a><br>
TikTok: <a href="https://www.tiktok.com/@jed.lopez.mendoza.dev">@jed.lopez.mendoza.dev</a><br>
Portfolio: <a href="https://sajed-mendoza.onrender.com" target="_blank">sajed-mendoza.onrender.com</a>
</p>

<h2>🌐 Related Links</h2>
<ul>
  <li>📦 Repository: <a href="https://github.com/qppd/Smart-Water-Guard">Smart Water Guard</a></li>
  <li>🔧 Prototype Hardware: <em>Private / Internal</em></li>
</ul>
