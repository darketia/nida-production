class MagicViewFilters {
  def filters = {
    all(controller: '*') {
      before = { model ->
        def tsService = grailsApplication.mainContext.getBean('tsService')

        //log
//        if(params.controller != 'assets') log.info("${params.controller}/${params.action} by ${tsService.currentUser?.getFullString()}")

        if (!request['currentUser']) {
          request['currentUser'] = tsService.currentUser
        }


      }
    }
  }
}
