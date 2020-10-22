package pro.akvel.spring.converter;

import pro.akvel.spring.converter.xml.ConfugurationSeacher;

/**
 * FIXME
 *
 * @author akvel
 * @since 12.08.2020
 */
public class Convertor {

    public static void main(String[] args) {
        ConfugurationSeacher scanner = new ConfugurationSeacher("src/test/resources/pro/akvel/spring/converter");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
