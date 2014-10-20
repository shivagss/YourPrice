Parse.Cloud.afterSave("Message", function(request) {
  var message = request.object;
  var bid = message.get("bid");
  var item = message.get("item");
  var body = message.get("body");
  var senderId = message.get("sender").id;

  item.fetch({
    success: function(updatedItem) {

      bid.fetch({
        success: function(updatedBid) {
          var receiverId;
          // find who we need to send the notification to
          var sellerId = updatedItem.get("createdBy").id;
          var buyerId = updatedBid.get("createdBy").id;
          if (senderId == sellerId) {
            // send to buyer
            receiverId = buyerId;
          }
          if (senderId == buyerId) {
            // send to seller
            receiverId = sellerId;
          }

          var query = new Parse.Query(Parse.Installation);
          query.equalTo('userId', receiverId);

          Parse.Push.send({
            where: query,
            data: {
              alert: body,
              type: "message",
              bidId: bid.id,
              itemId: item.id,
              senderId: senderId
            }
          }, {
            success: function() {
              // Push was successful
              console.log("Sent bid push notification to " + seller.id + " successfully");
            },
            error: function(error) {
              // Handle error
              console.log("Error sending bid push notification to " + seller.id);
            }
          });


        },
        error: function(myObject, error) {
            console.error("bid object not found");
        }
      });

    },
    error: function(myObject, error) {
        console.error("item object not found");
    }
  });

});



Parse.Cloud.afterSave("Bid", function(request) {
  var bid = request.object;
  var bidId = bid.id;
  var itemId = bid.get("itemId");
  var bidderId = bid.get("createdBy").id;
  var price = bid.get("price");
  var state = bid.get("state");
  query = new Parse.Query("Item");
  query.get(itemId, {
    success: function(item) {
      var query = new Parse.Query(Parse.Installation);
      var message = "";

      if (state == "pending") {
        // post push notification for new bid to seller
        var seller = item.get("createdBy");
        query.equalTo('user', seller);

        message = "Your got an offer of $" + price;

      } else {
        // accepted or rejected
        query.equalTo('userId', bidderId);

        message = "Your offer of $" + price + " got " + state;
      }

      // send push notification
      Parse.Push.send({
        where: query,
        data: {
          alert: message,
          type: "bid",
          bidId: bidId,
          senderId: bidderId,
          itemId: itemId
        }
      }, {
        success: function() {
          // Push was successful
          console.log("Sent bid push notification to " + seller.id + " successfully");
        },
        error: function(error) {
          // Handle error
          console.log("Error sending bid push notification to " + seller.id);
        }
      });


    },
    error: function(error) {
      console.error("Got an error " + error.code + " : " + error.message);
    }
  });
});


function getCommenters(itemId, callback) {
  query = new Parse.Query("Comment");
  query.equalTo("itemId", itemId);
  query.find({
    success: function(results) {
      var commenters = {}
      for (var i = 0; i < results.length; i++) { 
        var object = results[i];
        commenters[object.get('createdBy').id] = 1;
      }

      callback(Object.keys(commenters), null);
    },
    error: function(error) {
      console.error("Got an error " + error.code + " : " + error.message);
      callback(null, error);
    }
  });
}

Parse.Cloud.afterSave("Comment", function(request) {
  var itemId = request.object.get("itemId");
  var commentId = request.object.id
  var body = request.object.get("body");
  var commenterId = request.object.get("createdBy").id;

  // get all users in comment
  getCommenters(itemId, function(commenters, error) {
    if (error) {
      console.error("Error getting commenters");
    } else {
      query = new Parse.Query("Item");
      query.get(itemId, {
        success: function(item) {
          var seller = item.get("createdBy").id;

          // add seller to commenters list
          if (commenters.indexOf(seller) < 0) {
            commenters.push(seller);
          }

          // remove the commenter from the list
          var commenterIndex = commenters.indexOf(commenterId);
          if (commenterIndex >= 0) {
            commenters.splice(commenterIndex, 1);
          }

          console.log("commenters = " + commenters);

          // post push notification for to all commenters
          var query = new Parse.Query(Parse.Installation);
          query.containedIn('userId', commenters);
           
          Parse.Push.send({
            where: query, // Set our Installation query
            data: {
              alert: body,
              type: "comment",
              itemId: itemId,
              senderId: commenterId,
              commentId: commentId
            }
          }, {
            success: function() {
              // Push was successful
              console.log("Sent comment push notification to " + seller + " successfully");
            },
            error: function(error) {
              // Handle error
              console.log("Error sending comment push notification to " + seller);
            }
          });
        },
        error: function(error) {
          console.error("Got an error " + error.code + " : " + error.message);
        }
      });
    }
  });

});