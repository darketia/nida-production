import nida.production.MyUserDetailsService
import org.springframework.web.servlet.i18n.FixedLocaleResolver
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import nida.production.Constants
import nida.production.CustomPropertyEditorRegistrar

// Place your Spring DSL code here
beans = {
  customPropertyEditorRegistrar(CustomPropertyEditorRegistrar)
  userDetailsService(MyUserDetailsService)

  localeResolver(FixedLocaleResolver) {
    defaultLocale = Constants.LOCALE
    java.util.Locale.setDefault(defaultLocale)
  }
}
