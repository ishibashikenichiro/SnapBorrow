$(document).ready(function(){
  $("input[id=login_id]").focus(function(){
    $("#info_input_login_id").fadeIn();
  });
  $("input[id=login_id]").blur(function(){
    $("#info_input_login_id").fadeOut();
  });

  $("input[id=nickname]").focus(function(){
    $("#info_input_nickname").fadeIn();
  });
  $("input[id=nickname]").blur(function(){
    $("#info_input_nickname").fadeOut();
  });

  $("input[id=password]").focus(function(){
    $("#info_input_password").fadeIn();
  });
  $("input[id=password]").blur(function(){
    $("#info_input_password").fadeOut();
  });

  $("input[id=mail]").focus(function(){
    $("#info_input_mail").fadeIn();
  });
  $("input[id=mail]").blur(function(){
    $("#info_input_mail").fadeOut();
  });

  $("input[id=password_confirmation]").focus(function(){
    $("#info_input_password_confirmation").fadeIn();
  });
  $("input[id=password_confirmation]").blur(function(){
    $("#info_input_password_confirmation").fadeOut();
  });

  $("input[id=group_name]").focus(function(){
    $("#info_input_group_name").fadeIn();
  });
  $("input[id=group_name]").blur(function(){
    $("#info_input_group_name").fadeOut();
  });
  $("input[id=shelf_name]").focus(function(){
    $("#info_input_shelf_name").fadeIn();
  });
  $("input[id=shelf_name]").blur(function(){
    $("#info_input_shelf_name").fadeOut();
  });
});

var add_input_button_id = 1;
function addInput() {
  var o = document.getElementById('add-input-button'+ add_input_button_id);
  var q = document.getElementById('invite-email-div-' + (add_input_button_id + 1))
  o.style.display = "none";
  q.style.display = "";
  add_input_button_id += 1;
}