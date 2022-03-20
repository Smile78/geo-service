
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.netology.entity.Country;
import ru.netology.entity.Location;
import ru.netology.geo.GeoService;
import ru.netology.geo.GeoServiceImpl;
import ru.netology.i18n.LocalizationService;
import ru.netology.i18n.LocalizationServiceImpl;
import ru.netology.sender.MessageSender;
import ru.netology.sender.MessageSenderImpl;


import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {


    @Test
    @DisplayName("COPY mAIN")
    void COPY_mAIN() {
        GeoService geoService = new GeoServiceImpl();
        LocalizationService localizationService = new LocalizationServiceImpl();
        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.123.12.19");

        Assertions.assertEquals("Добро пожаловать", messageSender.send(headers));
    }


    //check kirilc2
    // metod 2 check symbols kirilic / latin / dig
    public String kirilc2(char ch) {

        if (Character.isDigit(ch)) {
            return "dig";

        } else if (Character.isAlphabetic(ch)) {

            if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.CYRILLIC)) {
                return "kir";
            } else if (Character.UnicodeBlock.of(ch).equals(Character.UnicodeBlock.BASIC_LATIN)) {
                return "lat";
            }
        }
        return "err";
    }


    //Поверить, что MessageSenderImpl всегда отправляет только русский текст, если ip относится к российскому сегменту адресов.
    @Test
    @DisplayName("msg sender test. that sender will send rus text for Rus counrty")
    void msg_Sender_Test_Rus_Text_for_Rus_Cntry() {

        GeoService geoService = Mockito.mock(GeoServiceImpl.class);
        Mockito.when(geoService.byIp("172.")).thenReturn(new Location("Moscow", Country.RUSSIA, null, 0));
        Location c = geoService.byIp("172.");


        LocalizationService localizationService = Mockito.mock(LocalizationServiceImpl.class);
        Mockito.when(localizationService.locale(Country.RUSSIA)).thenReturn("Добро пожаловать");


        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "172.");


        String stringa = messageSender.send(headers);
        System.out.println(stringa);

        String c1 = kirilc2(stringa.charAt(0));

        assertEquals(c1, "kir");

    }


    //Поверить, что MessageSenderImpl всегда отправляет только английский текст, если ip относится к американскому сегменту адресов.
    @Test
    @DisplayName("msg sender test. that sender will send LAt text for US  ")
    void msg_Sender_Test_Lat_Text_for_us() {


        GeoService geoService = Mockito.mock(GeoServiceImpl.class);
        Mockito.when(geoService.byIp("96.")).thenReturn(new Location("New York", Country.USA, null, 0));
        Location c = geoService.byIp("96.");


        LocalizationService localizationService = Mockito.mock(LocalizationServiceImpl.class);
        Mockito.when(localizationService.locale(Country.USA)).thenReturn("Welcome");


        MessageSender messageSender = new MessageSenderImpl(geoService, localizationService);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(MessageSenderImpl.IP_ADDRESS_HEADER, "96.");

        String stringa = messageSender.send(headers);
        System.out.println(stringa);

        String c1 = kirilc2(stringa.charAt(0));
        assertEquals(c1, "lat");


    }


    //Написать тесты для проверки определения локации по ip (класс GeoServiceImpl)
    //Проверить работу метода public Location byIp(String ip)


    private static Stream<Arguments> listIPndLoc() {
        return Stream.of(
                Arguments.of("172.0.32.11", new Location("Moscow", Country.RUSSIA, "Lenina", 15)),
//                Arguments.of("172.0.32.11", "Moscow", Country.RUSSIA, "Lenina", 15)
                Arguments.of("96.44.183.149", new Location("New York", Country.USA, " 10th Avenue", 32)),
                Arguments.of("172.", new Location("Moscow", Country.RUSSIA, null, 0)),
                Arguments.of("172.2", new Location("Moscow", Country.RUSSIA, null, 0)),
                Arguments.of("96.", new Location("New York", Country.USA, null, 0)),
                Arguments.of("96.2", new Location("New York", Country.USA, null, 0))
        );
    }


    @ParameterizedTest()
    @DisplayName("test of method byIp in GeoServiceImpl")
    @MethodSource("listIPndLoc")
    void method_byIp_test(String ip, Location locChecked1) {
        GeoService geo = new GeoServiceImpl();
        Location loc1 = geo.byIp(ip);
        assertEquals(loc1, locChecked1);

    }


    //Написать тесты для проверки возвращаемого текста (класс LocalizationServiceImpl)
    //Проверить работу метода public String locale(Country country)

    private static Stream<Arguments> listCountry() {
        return Stream.of(
                Arguments.of(Country.RUSSIA, "Добро пожаловать"),
                Arguments.of(Country.USA, "Welcome"),
                Arguments.of(Country.GERMANY, "Welcome"),
                Arguments.of(Country.BRAZIL, "Welcome")

        );
    }

    @ParameterizedTest()
    @DisplayName("test of method locale in LocalizationServiceImpl")
    @MethodSource("listCountry")
    void method_locale_test(Country countrySpel, String resltGret) {
        LocalizationService leo = new LocalizationServiceImpl();
        String cntr1 = leo.locale(countrySpel);
        assertEquals(cntr1, resltGret);
    }

    
}