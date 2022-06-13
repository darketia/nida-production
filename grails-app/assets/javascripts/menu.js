/*
*=require jquery
*/

$(function(){
  $.fn.textWidth = function(){
    var sensor = $('<div />').css({margin: 0, padding: 0});
    $(this).append(sensor);
    var width = sensor.width();
    sensor.remove();
    return width;
  };

  /**
   * ซ่อนเมนูถ้าไม่มี sub menu
   */
  $('.nav-menu ul').filter(
      function () {
        return $(this).children('li[class!="dropdown-header"][class!="divider"]').length == 0;
      }
  ).parent().remove()

  $('.nav-menu ul').filter(
      function () {
        return $(this).children('li[class!="dropdown-header"][class!="divider"]').length == 0;
      }
  ).parent().remove()

  $('.nav-menu ul li.dropdown-header').each(
      function () {
        var header = $(this)
        var next = header.next()
        if(next.html()===undefined || next.attr('class') == 'divider' || next.attr('class') == 'dropdown-header'){
          if(next){
            next.remove()
          }
          header.remove()
        }
      }
  )


  $(".dropdown-menu-large").each(
    function(){
      var ddl = $(this)
      var maxWidth = 0;
      ddl.children().each(
          function(){
            maxWidth += 395;
          }
      )
      ddl.css('width', maxWidth);
    })


})