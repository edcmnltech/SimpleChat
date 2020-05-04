
var url = "http://0.0.0.0:8081"

var items = $("#chatrooms");
function reloadJson(){ $.getJSON(url+"/chatrooms", function(data){
      $.each( data, function( key, val ) {
      items.prepend( "<li class='list-group-item' id='" + key + "'><a href='chat-client.html?id="+val.id.value+"&name="+val.name.value+"'>" + val.name.value + "</a></li>" );
      });
});
}
reloadJson();

var $add = $("#add");
var $chatroomname = $("#chatroomname");
var $nickname = $("#nickname");
$add.removeAttr();

$("#addRoomForm").submit(function(event){
    event.preventDefault();
    var post_url = url+"/chatrooms";
    var pass = { value: $("#password").val() }
    var aaa = {
        "name": {
            "value": $("#chatroomname").val()
        },
        "creator": {
            "value": $("#nickname").val()
        },
        "password": {
            "value": $("#password").val()
        },
        "id": {
            "value": 0
        }
    }

    $.ajax({
        url: post_url,
        type: 'post',
        contentType: 'application/json',
        success: function(result, status, xhr){
            $("#chatrooms").fadeOut(500, function() {
                $chatroomname.text = "";
                $nickname.text = "";
                $("#chatrooms").empty();
                reloadJson();
                $("#chatrooms").fadeIn().delay(500);
            });
            console.log("Success saving of new chatroom");
        },
        error: function(jqXHR, textStatus, errorThrown) {
           console.log("Error saving of new chatroom" + textStatus, errorThrown);
        },
        data: JSON.stringify(aaa)
    });

});
