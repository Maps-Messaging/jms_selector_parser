package io.mapsmessaging.selector.extensions;

import com.github.javafaker.Faker;
import io.mapsmessaging.selector.ParseException;
import io.mapsmessaging.selector.SelectorParser;
import io.mapsmessaging.selector.operators.ParserExecutor;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.junit.jupiter.api.Test;

public class StreamExample {

  private static final int LIST_SIZE = 10000;
  private final static List<Address> addressList =buildList();

  @Test
  public void simpleStream1() throws ParseException {
    ParserExecutor executor = SelectorParser.compile("state = 'Alaska'");
    long alaskanAddresses = addressList.stream().filter(executor::evaluate).count();
    System.err.println("Alaskan : "+alaskanAddresses);
  }

  @Test
  public void simpleStream2() throws ParseException {
    ParserExecutor executor = SelectorParser.compile("state IN ('Alaska', 'Hawaii')");
    long alaskanAddresses = addressList.stream().filter(executor::evaluate).count();
    System.err.println("Alaskan OR Hawaii : "+alaskanAddresses);
  }

  @Test
  public void simpleParallel() throws ParseException {
    ParserExecutor executor = SelectorParser.compile("state IN ('Alaska', 'Hawaii')");
    long alaskanAddresses = addressList.parallelStream().filter(executor::evaluate).count();
    System.err.println("Alaskan OR Hawaii : "+alaskanAddresses);
  }

  private static List<Address> buildList(){
    List<Address> addressList = new ArrayList<>();
    Faker faker = new Faker();
    for (int x = 0; x < LIST_SIZE; x++) {
      addressList.add(new Address(faker.address()));
    }
    return addressList;
  }

  public static class Address{
    @Getter final String street;
    @Getter final String suburb;
    @Getter final String zipCode;
    @Getter final String state;


    public Address(com.github.javafaker.Address address) {
      this.state = address.state();
      this.street = address.streetAddress();
      this.suburb = address.city();
      this.zipCode = address.zipCode();

    }
  }
}
