package com.way.plg;

import java.util.Arrays;
import java.util.List;

import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.MemberValueVisitor;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class ListingMemberValueVisitor implements MemberValueVisitor {
    private final List<String> values;

    private ListingMemberValueVisitor(List<String> values) {
        this.values = values;
    }

    @Override
    public void visitStringMemberValue(StringMemberValue node) {
        values.add(node.getValue());
    }

    @Override
    public void visitShortMemberValue(ShortMemberValue node) {
        values.add(String.valueOf(node.getValue()));
    }

    @Override
    public void visitLongMemberValue(LongMemberValue node) {
        values.add(String.valueOf(node.getValue()));
    }

    @Override
    public void visitIntegerMemberValue(IntegerMemberValue node) {
        values.add(String.valueOf(node.getValue()));
    }

    @Override
    public void visitFloatMemberValue(FloatMemberValue node) {
        values.add(String.valueOf(node.getValue()));
    }

    @Override
    public void visitEnumMemberValue(EnumMemberValue node) {
        values.add(node.getValue());
    }

    @Override
    public void visitDoubleMemberValue(DoubleMemberValue node) {
        values.add(String.valueOf(node.getValue()));
    }

    @Override
    public void visitClassMemberValue(ClassMemberValue node) {
        values.add(node.getValue());
    }

    @Override
    public void visitCharMemberValue(CharMemberValue node) {
        values.add(String.valueOf(node.getValue()));
    }

    @Override
    public void visitByteMemberValue(ByteMemberValue node) {
        values.add(String.valueOf(node.getValue()));
    }

    @Override
    public void visitBooleanMemberValue(BooleanMemberValue node) {
        values.add(String.valueOf(node.getValue()));
    }

    @Override
    public void visitArrayMemberValue(ArrayMemberValue node) {
        MemberValue[] nestedValues = node.getValue();
        for (MemberValue v : nestedValues) {
            v.accept(new ListingMemberValueVisitor(values) {
                @Override
                public void visitArrayMemberValue(ArrayMemberValue node) {
                    values.add(Arrays.toString(node.getValue()));
                }
            });
        }
    }

    @Override
    public void visitAnnotationMemberValue(AnnotationMemberValue node) {
        values.add(String.valueOf(node.getValue()));
    }
}
