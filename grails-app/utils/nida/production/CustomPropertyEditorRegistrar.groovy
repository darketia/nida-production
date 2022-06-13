package nida.production

import org.springframework.beans.PropertyEditorRegistrar
import org.springframework.beans.PropertyEditorRegistry
import org.springframework.context.i18n.LocaleContextHolder

class CustomPropertyEditorRegistrar implements PropertyEditorRegistrar {
  static final LOCALE = LocaleContextHolder.locale

  @Override
  void registerCustomEditors(PropertyEditorRegistry registrar) {
//    registrar.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy", LOCALE), true))
  }
}
