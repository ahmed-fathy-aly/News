About
--------
Displays the news feed from a fixed RSS(BBC News)

Features
------------
- updates every hour to fetch the latest news
- uses offline storage
- marks the already read feed items
- customized layout for screens with larger widths

Architecture
-----------------
MVP is used
- the view layer is the list and details activities and their fragments
- the presenter layer is the feed list and details presenters which present the data to the view layer
- the business logic is contained inside the interactors(read feed and sync feed) which are used by the presenters
- Local and Remote data sources are used by the presenters to fetch and update the data.

Screenshots
-----------------
<a href="http://i.imgur.com/nxMcaYl.png"><img src="http://i.imgur.com/nxMcaYl.png" title="Feed List"  height="600"/></a>
<a href="http://i.imgur.com/FbCxZjV.png"><img src="http://i.imgur.com/FbCxZjV.png" title="Feed Details"  height="600"/></a>
<a href="http://i.imgur.com/V6pJPpe.png"><img src="http://i.imgur.com/V6pJPpe.png" title="List+Details"  height="600"/></a>

Libraries used
--------------------
- JUnit, Espresso, Mockito for testing
- Dagger for dependency injection
- retrofit for http requests
- butterknife for resource binding
- rxAndroid
- picasso for lazy loading the images
- eventbus
- leak canary for detecting leaks
- timber for logging


