package com.tranek.chivalryserverbrowser;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;


/**
 * This class will check for any invalid input and present 
 * a Dialog Message to user, for entering appropriate input.
 * you can let it make sound when user tries to enter the
 * invalid input. Do see the beep() part for that inside 
 * the class's body.
 * 
 * @see "http://stackoverflow.com/questions/9477354/how-to-allow-introducing-only-digits-in-jtextfield"
 */
class DocumentInputFilter extends DocumentFilter
{
    public void insertString(FilterBypass fb
                , int offset, String text, AttributeSet as) throws BadLocationException {
        int len = text.length();
        if (len > 0) {
            /* Here you can place your other checks
             * that you need to perform and do add
             * the same checks for replace method
             * as well.
             */
            if (Character.isDigit(text.charAt(len - 1))) {
                super.insertString(fb, offset, text, as);
            }
        }                                               
    }

    public void replace(FilterBypass fb, int offset
                        , int length, String text, AttributeSet as) throws BadLocationException {
        int len = text.length();
        if (len > 0) {
            if (Character.isDigit(text.charAt(len - 1))) {
                super.replace(fb, offset, length, text, as);
            }
        }                                               
    }
}