package ru.vas7n.va.widgets

import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.TextField

class MaskField : TextField() {

    companion object {
        // позиция в маске позволит ввести только цифры
        const val MASK_DIGIT = 'D'
        // позиция в маске позволит ввести буквы и цифры
        const val MASK_DIG_OR_CHAR = 'W'
        // позиция в маске позволит ввести только буквы
        const val MASK_CHARACTER = 'A'

        const val WHAT_MASK_CHAR = '#'
        const val WHAT_MASK_NO_CHAR = '-'
        const val PLACEHOLDER_CHAR_DEFAULT = '_'

        private class Position(val mask: Char, private val whatMask: Char, val placeholder: Char) {
            val isPlainCharacter: Boolean
                get() = whatMask == WHAT_MASK_CHAR
            fun isCorrect(c: Char): Boolean {
                when (mask) {
                    MASK_DIGIT -> return Character.isDigit(c)
                    MASK_CHARACTER -> return Character.isLetter(c)
                    MASK_DIG_OR_CHAR -> return Character.isLetter(c) || Character.isDigit(c)
                }
                return false
            }
        }
    }

    private var objectMask: MutableList<Position> = mutableListOf()

    // простой текст без применения маски
    private var _plainText: StringProperty? = null
    private fun plainTextProperty(): StringProperty =
        _plainText?.let {
            return it
        } ?: SimpleStringProperty(this, "plainText", "").apply {
            _plainText = this
            return this
        }
    var plainText: String
        get() = plainTextProperty().get()
        set(value) {
            plainTextProperty().set(value)
            updateShowingField()
        }

    // это сама маска видимая в поле ввода
    private var _mask: StringProperty? = null
    private fun maskProperty(): StringProperty =
        _mask?.let {
            return it
        } ?: SimpleStringProperty(this, "mask", "").apply {
            _mask = this
            return this
        }
    var mask: String
        get() = maskProperty().get()
        set(value) {
            maskProperty().set(value)
            rebuildObjectMask()
            updateShowingField()
        }

    // если маска должна отображать символы которые зарезервированы для маски,
    // то задается дополнительная подсказка где символ маски, а где просто символ
    private var _whatMask: StringProperty? = null
    private fun whatMaskProperty(): StringProperty =
        _whatMask?.let {
            return it
        } ?: SimpleStringProperty(this, "whatMask", "").apply {
            _whatMask = this
            return this
        }
    var whatMask: String
        get() = whatMaskProperty().get()
        set(value) {
            whatMaskProperty().set(value)
            rebuildObjectMask()
            updateShowingField()
        }

    // это символы замещения
    private var _placeholder: StringProperty? = null
    private fun placeholderProperty(): StringProperty =
        _placeholder?.let {
            return it
        } ?: SimpleStringProperty(this, "placeholder", "").apply {
            _placeholder = this
            return this
        }
    var placeholder: String
        get() = placeholderProperty().get()
        set(value) {
            placeholderProperty().set(value)
            rebuildObjectMask()
            updateShowingField()
        }

    private fun interpretMaskPositionInPlainPosition(posMask: Int): Int {
        return objectMask.subList(0, posMask).count {
            it.isPlainCharacter
        }
    }

    override fun replaceText(start: Int, end: Int, text: String) {
        val plainStart = interpretMaskPositionInPlainPosition(start)
        val plainEnd = interpretMaskPositionInPlainPosition(end)
        val plainText1 =
            if (plainText.length > plainStart) plainText.substring(0, plainStart) else plainText
        val plainText2 =
            if (plainText.length > plainEnd) plainText.substring(plainEnd) else ""
        plainText = plainText1 + text + plainText2
    }

    // формирует список объектов Position по каждому символу маски
    private fun rebuildObjectMask() {
        objectMask.clear()
        for (i in mask.indices) {
            val m: Char = mask[i]
            var w = WHAT_MASK_CHAR
            //var p = PLACEHOLDER_CHAR_DEFAULT
            if (i < whatMask.length) {
                //конкретно указано символ маски это или нет
                if (whatMask[i] != WHAT_MASK_CHAR) w = WHAT_MASK_NO_CHAR
            } else {
                //так как не указано что за символ - понимаем самостоятельно
                //и если символ не находится среди символов маски - то это считается простым литералом
                if (m != MASK_CHARACTER && m != MASK_DIG_OR_CHAR && m != MASK_DIGIT)
                    w = WHAT_MASK_NO_CHAR
            }
            val p = if (i < placeholder.length) placeholder[i] else PLACEHOLDER_CHAR_DEFAULT
            objectMask.add(Position(m, w, p))
        }
    }

    // функция как бы накладывает просто текст plainText на заданную маску, корректирует позицию каретки
    private fun updateShowingField() {
        var counterPlainCharInMask = 0
        var lastPositionPlainCharInMask = 0
        var firstPlaceholderInMask = -1
        var textMask: String? = ""
        var textPlain: String = plainText
        for (i in objectMask.indices) {
            val p: Position = objectMask[i]
            if (p.isPlainCharacter) {
                if (textPlain.length > counterPlainCharInMask) {
                    var c = textPlain[counterPlainCharInMask]
                    while (!p.isCorrect(c)) {
                        //вырезаем то что не подошло
                        textPlain = textPlain.substring(0, counterPlainCharInMask) +
                                textPlain.substring(counterPlainCharInMask + 1)
                        c = if (textPlain.length > counterPlainCharInMask)
                            textPlain[counterPlainCharInMask]
                        else break
                    }
                    textMask += c
                    lastPositionPlainCharInMask = i
                } else {
                    textMask += p.placeholder
                    if (firstPlaceholderInMask == -1) firstPlaceholderInMask = i
                }
                counterPlainCharInMask++
            } else {
                textMask += p.mask
            }
        }
        text = textMask
        if (firstPlaceholderInMask == -1) firstPlaceholderInMask = 0
        val caretPosition = if (textPlain.isNotEmpty())
            lastPositionPlainCharInMask + 1 else firstPlaceholderInMask
        selectRange(caretPosition, caretPosition)
        if (textPlain.length > counterPlainCharInMask)
            textPlain = textPlain.substring(0, counterPlainCharInMask)
        if (textPlain != plainText) plainText = textPlain
    }
}