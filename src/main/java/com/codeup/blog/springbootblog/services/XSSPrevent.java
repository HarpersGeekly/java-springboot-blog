package com.codeup.blog.springbootblog.services;
import org.springframework.web.util.HtmlUtils;
import java.beans.PropertyEditorSupport;

/**
 * Created by RyanHarper on 3/16/17.
 */
public class XSSPrevent extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        String out = "";
        if(text != null)
            out = HtmlUtils.htmlEscape(text.trim());

        setValue(out);
    }

    @Override
    public String getAsText() {
        String out = (String) getValue();
        if(out == null)
            out = "";
        return out;
    }
}
