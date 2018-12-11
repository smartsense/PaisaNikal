package aepsapp.easypay.com.aepsandroid.mantradevice

/**
 * Raw text, another words TextWithout mask characters
 */
class RawText {
    var text: String? = null
        private set

    init {
        text = ""
    }

    /**
     * text = 012345678, range = 123 => text = 0456789
     * @param range
     */
    fun subtractFromString(range: Range) {
        var firstPart = ""
        var lastPart = ""

        if (range.start > 0 && range.start <= text!!.length) {
            firstPart = text!!.substring(0, range.start)
        }
        if (range.end >= 0 && range.end < text!!.length) {
            lastPart = text!!.substring(range.end, text!!.length)
        }
        text = firstPart + lastPart
    }

    /**
     *
     * @param newString New String to be added
     * @param start Position to insert newString
     * @param maxLength Maximum raw text length
     * @return Number of added characters
     */
    fun addToString(newString: String?, start: Int, maxLength: Int): Int {
        var newString = newString
        var firstPart = ""
        var lastPart = ""

        if (newString == null || newString == "") {
            return 0
        } else if (start < 0) {
            throw IllegalArgumentException("Start position must be non-negative")
        } else if (start > text!!.length) {
            throw IllegalArgumentException("Start position must be less than the actual text length")
        }

        var count = newString.length

        if (start > 0) {
            firstPart = text!!.substring(0, start)
        }
        if (start >= 0 && start < text!!.length) {
            lastPart = text!!.substring(start, text!!.length)
        }
        if (text!!.length + newString.length > maxLength) {
            count = maxLength - text!!.length
            newString = newString.substring(0, count)
        }
        text = firstPart + newString + lastPart
        return count
    }

    fun length(): Int {
        return text!!.length
    }

    fun charAt(position: Int): Char {
        return text!![position]
    }
}
