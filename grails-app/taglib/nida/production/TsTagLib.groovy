package nida.production

import org.springframework.web.servlet.support.RequestContextUtils as RCU

import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.chrono.BuddhistChronology
import org.joda.time.chrono.GregorianChronology
import org.joda.time.format.DateTimeFormat
import org.springframework.context.NoSuchMessageException
import org.springframework.util.StringUtils
import java.text.DecimalFormat

class TsTagLib {
  /**
   * Outputs the given <code>Date</code> object in the specified format. If
   * the <code>date</code> is not given, then the current date/time is used.
   * If the <code>format</code> option is not given, then the date is output
   * using the default format.<br/>
   *
   * e.g., &lt;g:formatDate date="${myDate}" format="yyyy-MM-dd HH:mm" /&gt;<br/>
   *
   * @see java.text.SimpleDateFormat
   *
   * @emptyTag
   *
   * @attr date the date object to display; defaults to now if not specified
   * @attr format The formatting pattern to use for the date, see SimpleDateFormat
   * @attr formatName Look up format from the default MessageSource / ResourceBundle (i18n/*.properties file) with this key. If format and formatName are empty, format is looked up with 'default.date.format' key. If the key is missing, 'yyyy-MM-dd HH:mm:ss z' formatting pattern is used.
   * @attr type The type of format to use for the date / time. format or formatName aren't used when type is specified. Possible values: 'date' - shows only date part, 'time' - shows only time part, 'both'/'datetime' - shows date and time
   * @attr timeZone the time zone for formatting. See TimeZone class.
   * @attr locale Force the locale for formatting.
   * @attr style Use default date/time formatting of the country specified by the locale. See org.joda.time.format.DateTimeFormat#forStyle for explanation.
   * @attr dateStyle Set separate style for the date part.
   * @attr timeStyle Set separate style for the time part.
   */
  static namespace = "ts"

  Closure formatDate = { attrs ->

    def date
    def nullValue = attrs.remove('nullValue') ?: ''
    def locale = resolveLocale(attrs.locale)
    if (attrs.containsKey('date')) {
      date = attrs.date
      if (date == null) {
        out << nullValue
        return
      }
    } else {
      date = new Date()
    }
    if (date instanceof java.util.Date) {
      date = new DateTime(date.getTime()).withChronology(locale.language == 'th' ? BuddhistChronology.instance : GregorianChronology.instance)
    }

    String timeStyle = null
    String dateStyle = null
    String style
    if (attrs.style != null) {
      style = attrs.style.toString().toUpperCase()
      timeStyle = style
      dateStyle = style
    }

    if (attrs.dateStyle != null) {
      dateStyle = attrs.dateStyle.toString().toUpperCase()
    }
    if (attrs.timeStyle != null) {
      timeStyle = attrs.timeStyle.toString().toUpperCase()
    }
    String type = attrs.type?.toString()?.toUpperCase()
    def formatName = attrs.formatName
    def format = attrs.format
    def timeZone = attrs.timeZone
    if (timeZone != null) {
      if (!(timeZone instanceof DateTimeZone)) {
        timeZone = DateTimeZone.forID(timeZone as String)
      }
    } else {
      timeZone = DateTimeZone.getDefault()
    }

    def dateFormat
    if (!type) {
      if (!format && formatName) {
        format = messageHelper(formatName, null, null, locale)
        if (!format) {
          throwTagError("Attribute [formatName] of Tag [formatDate] specifies a format key [$formatName] that does not exist within a message bundle!")
        }
      } else if (!format) {
        format = messageHelper('date.format', { messageHelper('default.date.format', 'yyyy-MM-dd HH:mm:ss z', null, locale) }, null, locale)
      }
      dateFormat = DateTimeFormat.forPattern(format).withLocale(locale).withZone(timeZone)
    } else {
      if (type == 'DATE') {
        dateFormat = DateTimeFormat.forStyle((dateStyle ?: "S") + "-").withZone(timeZone).withLocale(locale)
      } else if (type == 'TIME') {
        dateFormat = DateTimeFormat.forStyle("-" + (timeStyle ?: "S")).withZone(timeZone).withLocale(locale)
      } else { // 'both' or 'datetime'
        dateFormat = DateTimeFormat.forStyle(style ?: "SS").withZone(timeZone).withLocale(locale)
      }
    }

    out << dateFormat.print(date)
  }

  def resolveLocale(localeAttr) {
    def locale = localeAttr
    if (locale != null && !(locale instanceof Locale)) {
      locale = StringUtils.parseLocaleString(locale as String)
    }
    if (locale == null) {
      locale = RCU.getLocale(request)
      if (locale == null) {
        locale = Locale.getDefault()
      }
    }
    return locale
  }

  String messageHelper(code, defaultMessage = null, args = null, locale = null) {
    if (locale == null) {
      locale = RCU.getLocale(request)
    }
    def messageSource = grailsAttributes.applicationContext.messageSource
    def message
    try {
      message = messageSource.getMessage(code, args == null ? null : args.toArray(), locale)
    }
    catch (NoSuchMessageException e) {
      if (defaultMessage != null) {
        if (defaultMessage instanceof Closure) {
          message = defaultMessage()
        } else {
          message = defaultMessage as String
        }
      }
    }
    return message
  }

  def formatDec2OrDash = { attrs ->
    formatOrDash('default.dec2.format', attrs)
  }

  def formatDec3OrDash = { attrs ->
    formatOrDash('default.dec3.format', attrs)
  }

  def formatTimeFromMins = { attrs ->
    int hour = attrs.time / 60
    int min = attrs.time % 60
    out << g.formatNumber(number: hour, format: '00') + ':' + g.formatNumber(number: min, format: '00')
  }

  private def formatOrDash(formatName, attrs) {
    def number = attrs.number
    def zero = attrs.zero ?: '-'
    def df = new DecimalFormat(message(code: formatName))

    if (number == 0) {
      number = zero
    }

    if (number == null) {
      out << ''
    } else if (number instanceof Number) {
      out << df.format(number)
    } else {
      try {
        def n = new BigDecimal(number.toString())
        out << df.format(n)
      } catch (Exception e) {
        out << number.toString()
      }
    }
  }

  def formatTime = { attrs ->
    int hour = attrs.time ? (attrs.time as int) / 60 : 0
    int min = attrs.time ? (attrs.time as int) % 60 : 0
    out << g.formatNumber(number: hour, format: '00') + ':' + g.formatNumber(number: min, format: '00')
  }

  /**
   * Creates select with default value.
   *
   * @attr name REQUIRED the select name
   * @attr id the DOM element id - uses the name attribute if not specified
   * @attr from REQUIRED The list or range to select from
   * @attr keys A list of values to be used for the value attribute of each "option" element.
   * @attr optionKey By default value attribute of each &lt;option&gt; element will be the result of a "toString()" call on each element. Setting this allows the value to be a bean property of each element in the list.
   * @attr optionValue By default the body of each &lt;option&gt; element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be a bean property of each element in the list.
   * @attr value The current selected value that evaluates equals() to true for one of the elements in the from list.
   * @attr multiple boolean value indicating whether the select a multi-select (automatically true if the value is a collection, defaults to false - single-select)
   * @attr valueMessagePrefix By default the value "option" element will be the result of a "toString()" call on each element in the "from" attribute list. Setting this allows the value to be resolved from the I18n messages. The valueMessagePrefix will be suffixed with a dot ('.') and then the value attribute of the option to resolve the message. If the message could not be resolved, the value is presented.
   * @attr noSelection A single-entry map detailing the key and value to use for the "no selection made" choice in the select box. If there is no current selection this will be shown as it is first in the list, and if submitted with this selected, the key that you provide will be submitted. Typically this will be blank - but you can also use 'null' in the case that you're passing the ID of an object
   * @attr disabled boolean value indicating whether the select is disabled or enabled (defaults to false - enabled)
   * @attr readonly boolean value indicating whether the select is read only or editable (defaults to false - editable)
   */
  Closure selectWithDefault = { attrs ->
    def defaultValue = attrs.from?.find{it.isDefault}
    if(defaultValue && !attrs.value){
      attrs.value = attrs.optionKey? defaultValue[(attrs.optionKey)]: defaultValue.id
    }
    out << g.select(attrs)
  }

  /**
   * Creates customer auto complete input.
   *
   * @attr name REQUIRED the field name
   * @attr value the field value
   * @attr required
   * @attr callback the callback function
   */
  Closure autoComplete = { attrs ->
    out << render(template: '/shared/autoComplete', model: [name: attrs.name, value: attrs.value, action:attrs.action, required: attrs.containsKey('required'), callback: attrs.callback])
  }

  /**
   * A helper tag for creating radio button groups.
   *
   * @attr name REQUIRED The name of the group
   * @attr values REQUIRED The list values for the radio buttons
   * @attr value The current selected value
   * @attr labels Labels for each value contained in the values list. If this is ommitted the label property on the iterator variable (see below) will default to 'Radio ' + value.
   */
  Closure radioGroup = { attrs, body ->
    def value = attrs.remove('value')
    def values = attrs.remove('values')
    def labels = attrs.remove('labels')
    def name = attrs.remove('name')
    values.eachWithIndex { val, idx ->
      def it = new Expando()
      it.radio = new StringBuilder("<input type=\"radio\" name=\"${name}\" ")
      if ((value == null && val == null) ||
          value?.toString().equals(val.toString())) {
        it.radio << 'checked="checked" '
      }
      it.radio << "value=\"${val.toString().encodeAsHTML()}\" "

      // process remaining attributes
      outputAttributes(attrs, it.radio)
      it.radio << "/>"

      it.label = labels == null ? 'Radio ' + val : labels[idx]

      out << body(it)
      out.println()
    }
  }

  Closure selectWithOptGroup = { attrs, body ->
    def from = attrs.remove('from')
    def groupBy = attrs.remove('groupBy')
    def optionKey = attrs.remove('optionKey')
    def optionValue = attrs.remove('optionValue')
    def selected = attrs.remove('value')
    Set optGroupSet = new TreeSet()
    def name = attrs.remove('name')

    //Make that sure the object we're working with, has groupBy, optionKey, and optionValue properties that actually
    //exist

    if(from.size() > 0) {
      if(!from[0].properties[groupBy]) {
        throw new MissingPropertyException("No such property: ${groupBy} for class: ${from[0].class.name}")
      }

      else if(!from[0].properties[optionKey]) {
        throw new MissingPropertyException("No such property: ${optionKey} for class: ${from[0].class.name}")
      }


    }

    from.each {
      optGroupSet.add(it.properties[groupBy])
    }

    out << "<select name=\"${name}\" "

    attrs.each{key, value ->
      out << "${key}=\"${value.encodeAsHTML()}\" "
    }

    out << ">"

    optGroupSet.each{ optGroup->
      out << " <optgroup label=\"${optGroup.encodeAsHTML()}\">\n"
      for(option in from) {
        if(option.properties[groupBy].equals(optGroup)) {
          /*if (optionValue) {
            if (optionValue instanceof Closure) {
              writer << optionValue(el).toString().encodeAsHTML()
            }
            else {
              writer << el[optionValue].toString().encodeAsHTML()
            }
          }*/
          out << "  <option ${( option.properties[optionKey] in selected) ? 'selected="selected" ': ''} value=\"${option.properties[optionKey]}\">${option.toString()}</option>\n"
        }
      }
      out << " </optgroup>\n"
    }

    out << "</select>\n"
  }

  /**
   * Dump out attributes in HTML compliant fashion.
   */
  void outputAttributes(attrs, writer) {
    attrs.remove('tagName') // Just in case one is left
    attrs.each { k, v ->
      writer << k
      writer << '="'
      writer << v.encodeAsHTML()
      writer << '" '
    }
  }
}
