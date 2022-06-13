<g:set var="obj_suffix" value="${UUID.randomUUID().toString()}" scope="page"/>
<script type="text/javascript">
  $(function(){
    var searchObj = $("#${name.replace('.', '\\\\.').replace('[', '\\\\[').replace(']', '\\\\]')}-search-${obj_suffix}");
    var labelObj = $("#${name.replace('.', '\\\\.').replace('[', '\\\\[').replace(']', '\\\\]')}-label-${obj_suffix}");
    var idObj = $("#${name.replace('.', '\\\\.').replace('[', '\\\\[').replace(']', '\\\\]')}-id-${obj_suffix}");

    var cache = {};
    $(searchObj).autocomplete({
      source: function(request, response) {
        var term = request.term;
        if ( term in cache ) {
          response( cache[ term ] );
          return;
        }

        var _this = this;
        var req = {term: term};
        lastXhr = $.getJSON("${createLink(controller: 'ajaxHelper', action: "${action}")}", req,
          function(data, status, xhr) {
            cache[ term ] = data;
            response( data );
          }
        );
      },
      select: function(e, ui) {
        $(labelObj).val(ui.item.label);
        $(idObj).val(ui.item.id);
        $(searchObj).val(ui.item.code);
        <g:if test="${callback}">
          (${callback})(ui.item.id);
        </g:if>
      },
      change: function( event, ui ) {
        if(!ui.item){
          $(labelObj).val('');
          $(idObj).val('null');
          $(searchObj).val('');
          <g:if test="${callback}">
            (${callback})('');
          </g:if>
        }
      },
      create: function() {
        $(this).data('ui-autocomplete')._renderItem = function( ul, item ) {
          return $( "<li></li>" )
                .data( "item.autocomplete", item )
                .append( item.label )
                .appendTo( ul );
        }
      }
    });
  });
</script>

<div class="row autocomplete-input">
  <div class="col-sm-4">
    <input type="search" name="${name}-search-${obj_suffix}" id="${name}-search-${obj_suffix}" class="form-control input-search"
           value="${value?.code?.encodeAsHTML()}" ${required?"required=''":''} placeholder="${g.message(code: 'default.search.label', default: 'Search')}"/>
  </div>
  <div class="col-sm-8">
    <input type="text" name="${name}-label-${obj_suffix}" id="${name}-label-${obj_suffix}" class="form-control" readonly disabled="disabled" value="${value}"/>
  </div>
</div>
<g:hiddenField id="${name}-id-${obj_suffix}" name="${name}.id" value="${value?.id ?: 'null'}"/>
