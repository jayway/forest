package com.jayway.restfuljersey.samples.bank.helper;

import com.jayway.restfuljersey.samples.bank.model.Account;

import java.util.Collection;

/**
 */
public class HtmlHelper {

    public static String toLinks(Collection<Account> links) {
        StringBuilder sb = new StringBuilder();
        sb.append( "<ul>");
        for (Account account : links ) {
            sb.append("<li><a href=\"").append( account.getAccountNumber() ).append( "/\">").
            append( account.getAccountNumber() ).append("</a></li>");
        }
        sb.append( "</ul>");
        return sb.toString();
    }
}
