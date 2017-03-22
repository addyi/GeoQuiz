# GeoQuiz
GeoQuiz is a Project for [TH Bingen Coding Camp][video]

Das GeoQuiz ist ein Demo App für das Coding Camp der TH Bingen am 25./26. März 2017

[video]:https://youtu.be/KaD0M8ueda8

GeoQuiz ist ein Multiplayer Spiel, bei dem die beiden Spieler nacheinander eine Frage gestellt bekommen.
Jede Frage sucht immer einen Ort auf der Landkarte. Die Spieler haben dann die Aufgabe diesen Ort auf der
Landkarte zu markieren. Derjenige Spieler der am nächsten am gesuchten Ort liegt gewinnt die Runde. Am 
Anfang des Spiels kann man die Rundenanzahl und verfügbare Zeit einstellen, sowie die beiden Spielernamen
angeben.

##### tasks.json

```json
[
 {
    "question":"Wo wurde Linus Torvalds (Linux Erfinder) geboren?",
    "lat":60.169856,
    "long":24.938379,
    "answer": "Linus Torvalds wurde am 28. Dezember 1969 in Helsinki geboren.",
    "creator": "addyi"
  },
  {
    "question":"Wo gibt es die Original Sacher-Torte?",
    "lat":48.204059,
    "long":16.369804,
    "answer": "Die Original Sacher-Torte gibt es nur im Hotel Sacher in Wien.",
    "creator": "addyi"
  }
 ]
```

## Google API Key

Um die Google Maps Karten in der App angezeigt zu bekommen, ist es nötig sich einen API Key bei Google 
zu generieren. Dies ist unter der [Developer Console von Google][key] möglich. Der Key muss dann als 
Text String in dem Projekt eingebunden werden. In der Manifest Datei ist die benötigte String Referenz 
zu finden.

##### AndroidManifest.xml

```xml
<meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="@string/google_maps_key"/>
```

##### strings.xml

```xml
<string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">HIER KEY EINFÜGEN</string>
```
[key]:https://console.developers.google.com/


## Image Sources

### Image Source of winner.jpg

- [pixabay.com][winner]
- CC0 Public Domain
- date: 2017-03-14 12:02
- by pixabay user: 3dman_eu

[winner]:https://pixabay.com/en/cup-victory-winner-award-gold-1010916/  


### Image Source of app icon

- [pixabay.com][app_icon] modified with Android Studio Image Asset Assistant
- CC0 Public Domain
- date: 2017-03-17 19:30
- by pixabay user: villarreallevi
- original file: compass.png

[app_icon]:https://pixabay.com/en/compass-flat-icon-symbol-design-746122/
