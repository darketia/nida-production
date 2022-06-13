// This is a manifest file that'll be compiled into application.js.
//
// Any JavaScript file within this directory can be referenced here using a relative path.
//
// You're free to add application-wide JavaScript to this file, but it's generally better 
// to create separate JavaScript files as needed.
//
//= require jquery
//= require jquery-ui-1.11.2.custom/jquery-ui
//= require bootstrap
//= require_tree .
//= require_self
//= require select2/js/select2
//= require autocomplete/js/autocomplete
//= require bootstrap-datetimepicker-master/js/bootstrap-datetimepicker
//= require multi-select/js/multi-select
//= require jquery-quicksearch/jquery-quicksearch
//= require filestyle/js/bootstrap-filestyle.min
//= require nicEdit/nicEdit
//= require customd-jquery-number/number-format

if (typeof jQuery !== 'undefined') {
  (
      function ($) {
        $('#spinner').ajaxStart(function () {
          $(this).fadeIn();
        }).ajaxStop(function () {
          $(this).fadeOut();
        });

        $("input[type=number]").bind("mousewheel", function(){
          $(this).blur();
        });

        $.ajaxSetup({ cache: false });
      }
  )(jQuery);
}

