package com.mymoney.investment.config;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

import static com.mymoney.investment.utils.Constants.SHELL_PROMPT;

@Component
public class MyMoneyPromtProvider implements PromptProvider {
    @Override
    public AttributedString getPrompt() {
        return new AttributedString(
                SHELL_PROMPT, AttributedStyle.DEFAULT.foreground(AttributedStyle.BLUE));
    }
}
