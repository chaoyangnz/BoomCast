### About BoomCast

BoomCast is a simple Android app developed for paper COMP548 at Waikato University.
It is mainly to demonstrate the basics of SQLite, networking, sharing and much more.

---

This App is used to manage subscriptions of Podcast.

### Features

- Present podcast subscription list
- Show podcast details and its episodes
- Refresh podcast subscription to fetch latest episodes
- Add new podcast subscription by URL
- Stream podcast playback
- Usage statistics about daily listening time
- Search episodes in a podcast
- Share amazing episodes to other Apps

### Technical points

#### Requirement checklist

* SQLite: 3 tables (Podcast, Episode, UserData)
* Networking: integrating Volley, OkHttp3 and Gson. Analysing RSS XML and iTunes format
* Sharing: Share text data to other Apps
* Data processing: Recording user listening time and presenting some charts
* Searching: Supporting searching within a podcast
* 3rd party libraries: Volley, OkHttp, Sugar ORM, RxJava, Gson, Glide, Textdrawable, MarkdownView

#### Test environment

- SDK API 25
- Tested on Google Nexus 5X

### Notice

- Only iTunes Podcast source is supported.

- Some assets are from Internet and the original authors reserve the copyright.

### TODO

- Support episode downloading and offline playback
- Automatic refresh strategy

### Screen snapshot

<img src="snapshot1.jpeg" width="180" />
<img src="snapshot2.jpeg" width="180" />
<img src="snapshot3.jpeg" width="180" />
<img src="snapshot4.jpeg" width="180" />
<img src="snapshot5.jpeg" width="180" />