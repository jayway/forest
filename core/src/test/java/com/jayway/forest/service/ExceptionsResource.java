package com.jayway.forest.service;

import com.jayway.forest.constraint.Doc;
import com.jayway.forest.constraint.RolesNotInContext;
import com.jayway.forest.dto.IntegerDTO;
import com.jayway.forest.roles.Resource;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;

import static com.jayway.forest.core.RoleManager.addRole;

/**
 */
public class ExceptionsResource implements Resource {

    public String mappedchecked() throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    public String mappedunchecket() {
        throw new NullPointerException();
    }

    public String unmappedchecket() throws IOException {
        throw new IOException();
    }

    @Doc("Just to try using the constraint")
    @RolesNotInContext( IntegerDTO.class )
    public String unmappedunchecket() {
        throw new RuntimeException();
    }

    public void mappedcheckedcommand() throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    public void mappedunchecketcommand() {
        throw new NullPointerException();
    }

    public void unmappedchecketcommand() throws IOException {
        throw new IOException();
    }

    public void unmappedunchecketcommand() {
        addRole(Integer.class, "will throw IllegalArgument Exception");
    }

}
