# Preliminary Scoping Outline

## Section A.

**Group Members:**

* Sreejumon KP <sreejumon@gmail.com>
* Shiva sandeep Garlapati <shivagarlapati@gmail.com>
* Gabi Zuniga <gabiz@gapix.com>

**Mentor:** Vijay Sailappan <vijay.sailappan@gmail.com>

**App Name: InstaSell**

**What problem does this app address:**

* Casual sellers do not have a simple way of quickly putting items for sale today. Posting on Craigslist is cumbersome and items not sold within a few weeks it expire, requiring to repost items often.
* There is no adequate mobile buying experience for local buyers which encourage browsing without the need of knowing what you are looking for. With Craigslist the buying experience is dry and boring and it does not fit well the mobile form factor even for Craigslist mobile apps in the market today.


**How does this app solve that problem:**

InstaSell solves this problem by creating a mobile marketplace in which posting items is super simple and the buying experience leverages the browsing patterns from successful social apps.


**App Audience:**

* Local sellers with a mobile phone for posting items for sale.
* Local buyers looking for a specific item or just interested in searching for deals in the neighborhood or just curious of what neighbors or friends are selling.


**App Main Functions** (in order of priority):

1. Users login/register **(Login/Register)**
2. Post item for sale. **(Post Item for Sale)**
3. Feed with items for sale like Etsi. **(Browse/Search)**
4. Buyer can post a private offer for an item for sale.  **(Post Offer)**
5. Post and read comments  **(Detail/Comments)**
6. Browse user store. **(Store)**


## Section B.

**Steps for Login/Register:**
* User is presented with a registration/login screen. On the first run the registration screen is shown by default. On consecutives runs the app will auto login, but if the user logged out then the login screen is presented.
* On registration the user is requested: username, password, profile picture, location. The user is also asked to select categories of interest which will be used to populate its main item feed, similarly to Pinterest.
* Login screen requests: username and password.
* A forgot my password button is also presented to reset password.

**Steps for Post Item for Sale:**
* Display a screen including the following fields: caption, picture, desired price.
* Optionally (stretch goal) the sellers are presented with an option to use photo filters like instagram for making the beautiful pictures.
* Seller fills information and presses post button to post.
* Following post, the details/comments screen of the item is presented and the seller can add comments in the feed if he/she wishes to add more details.

**Steps for Browse/Search:**
* The browse screen is the main screen of the app and it resembles the Etsi screen.
* The user is presented with a feed of items for sale for browsing. Each item includes a picture and a caption.
* When an item is selected then the details/comments screen is presented.
* This screen’s menu includes a search option to search for specific items or categories.

**Steps for Detail/Comments:**
* This screen shows the image and caption with full dimensions.
* It also has a comment feed with an edit text box in the bottom to post comments similar to social apps.
* The menu of this screen has an offer button to post a private offer to the seller.

**Steps for Post Offer:**
* This screen has a field to enter the amount to offer for the item and a field for adding additional information to be sent to the user by email.


## Section C.

**Checklist of screens needed:**

* Splash (optional)
* Registration screen
* Login screen
* Post Item for Sale screen
* Item Feed screen
* Comment screen
* Store screen


**Requirements Checklist:**

Must have **at least** three separate "activities" or screens supporting user interaction

* The project has a total of 6 separate acitvities.

Must be **data-driven** with dynamic information or media being displayed

* All user, inventory and user comments is data driven data retrieved from the cloud. We will use Parse as the primary backend technology for the project.

Must use a **RESTful API** to source the data that is populated into the application

* While Parse precludes the need of interacting with a backend using RESTful API, it allows creating REST endpoints with 
“Cloud Code”. Specific functionality like comment feeds can be implemented using RESTful apis serviced by Parse. 
* In addition we can use AWS S3’s REST api for storing BLOBS in the cloud.

Must use **local persistence** either through files, preferences or SQLite

* All data will be persisted in SQLite for offline viewing.



