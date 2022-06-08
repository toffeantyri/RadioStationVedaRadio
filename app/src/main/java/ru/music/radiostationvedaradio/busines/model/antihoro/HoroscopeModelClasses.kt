package ru.music.radiostationvedaradio.busines.model.antihoro

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root


@Root(name = "horo")
class HoroscopeModelClasses {

    @get:Element(required = false, name = "date")
    @set:Element(required = false, name = "date")
    var date : Date? = null

    @get:ElementList(required = false, name = "aries")
    @set:ElementList(required = false, name = "aries")
    var aries : List<String>? = null

    @get:ElementList(required = false, name = "taurus")
    @set:ElementList(required = false, name = "taurus")
    var taurus : List<String>? = null

    @get:ElementList(required = false, name = "gemini")
    @set:ElementList(required = false, name = "gemini")
    var gemini : List<String>? = null

    @get:ElementList(required = false, name = "cancer")
    @set:ElementList(required = false, name = "cancer")
    var cancer : List<String>? = null

    @get:ElementList(required = false, name = "leo")
    @set:ElementList(required = false, name = "leo")
    var leo : List<String>? = null

    @get:ElementList(required = false, name = "virgo")
    @set:ElementList(required = false, name = "virgo")
    var virgo : List<String>? = null

    @get:ElementList(required = false, name = "libra")
    @set:ElementList(required = false, name = "libra")
    var libra : List<String>? = null

    @get:ElementList(required = false, name = "scorpio")
    @set:ElementList(required = false, name = "scorpio")
    var scorpio : List<String>? = null

    @get:ElementList(required = false, name = "sagittarius")
    @set:ElementList(required = false, name = "sagittarius")
    var sagittarius : List<String>? = null

    @get:ElementList(required = false, name = "capricorn")
    @set:ElementList(required = false, name = "capricorn")
    var capricorn : List<String>? = null

    @get:ElementList(required = false, name = "aquarius")
    @set:ElementList(required = false, name = "aquarius")
    var aquarius : List<String>? = null

    @get:ElementList(required = false, name = "pisces")
    @set:ElementList(required = false, name = "pisces")
    var pisces : List<String>? = null


}

@Root(name = "date")
class Date {

    @get:Attribute(required = false, name = "yesterday")
    @set:Attribute(required = false, name = "yesterday")
    var yesterday = ""

    @get:Attribute(required = false, name = "today")
    @set:Attribute(required = false, name = "today")
    var today = ""

    @get:Attribute(required = false, name = "tomorrow")
    @set:Attribute(required = false, name = "tomorrow")
    var tomorrow = ""

    @get:Attribute(required = false, name = "tomorrow02")
    @set:Attribute(required = false, name = "tomorrow02")
    var tomorrow02 = ""
}

@Root(name = "aries")
class Aries {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "taurus")
class Taurus {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "gemini")
class Gemini {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "cancer")
class Cancer {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "leo")
class Leo {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "virgo")
class Virgo {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "libra")
class Libra {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "scorpio")
class Scorpio {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "sagittarius")
class Sagittarius {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "capricorn")
class Capricorn {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "aquarius")
class Aquarius {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}

@Root(name = "pisces")
class Pisces {

    @get:Element(required = false, name = "yesterday")
    @set:Element(required = false, name = "yesterday")
    var yesterday : String? = null

    @get:Element(required = false, name = "today")
    @set:Element(required = false, name = "today")
    var today : String? = null

    @get:Element(required = false, name = "tommorow")
    @set:Element(required = false, name = "tommorow")
    var tommorow : String? = null

    @get:Element(required = false, name = "tommorow02")
    @set:Element(required = false, name = "tommorow02")
    var tommorow02 : String? = null
}


