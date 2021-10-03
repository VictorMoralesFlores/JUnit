package org.vmorales.junit5app.ejemplos.models;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.vmorales.junit5app.ejemplos.exceptions.DineroInsuficienteException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CuentaTest {
    Cuenta cuenta;
    TestReporter testReporter;
    TestInfo testInfo;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Antes de todas las pruebas");
        //this.cuenta = new Cuenta("Andres",new BigDecimal("1000.12345"));
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Después de todas las pruebas");
    }

    @BeforeEach
    void initMetodoTest( TestInfo testInfo, TestReporter testReporter )
    {
        this.testReporter = testReporter;
        this.testInfo = testInfo;
        this.cuenta = new Cuenta("Andres",new BigDecimal("1000.12345"));
        System.out.println( "Ejecutando " + testInfo.getDisplayName() + " " +
                testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " +testInfo.getTags());
    }

    @AfterEach
    void tearDown()
    {
        System.out.println("Finalizando el metodo de prueba");
    }

    @Tag("cuenta")
    @Nested
    class CuentaTestNombreSaldo
    {
        @Test
        @DisplayName("Prueba del nombre de la cuenta")
        void testNombreCuenta( ) {
            testReporter.publishEntry( testInfo.getTags().toString() );
            if( testInfo.getTags().contains("cuenta"))
            {
                testReporter.publishEntry("hacer algo con la etiqueta cuenta");
            }
            String esperado = "Andres";
            String real = cuenta.getPersona();
            assertNotNull( real, ()-> "La cuenta no puede ser nula" );
            assertEquals(esperado, real, ()-> "El nombre de la cuenta no es el que se esperaba");
            assertTrue(real.equals(esperado), ()-> "El nombre de la cuenta esperada debe ser igual a la real");
        }

        @Test
        @DisplayName("Probando el correcto funcinamiento al agregar saldo a la cuenta")
        void testSaldoCuenta()
        {
            assertNotNull( cuenta.getSaldo() );
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @Test
        @DisplayName("Probando que equals sobreecrito verifica correctamente que dos instancias se traten de la misma cuenta")
        void testReferenciaCuenta() {
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1000.12345"));
            //assertNotEquals( cuenta1, cuenta2);
            assertEquals( cuenta, cuenta2);
        }
    }


    @Nested
    @Tag("cuenta")
    class CuentaOperacionesTest
    {
        @Test
        void testDebitoCuenta()
        {
            cuenta.debito( new BigDecimal(100) );
            assertNotNull( cuenta.getSaldo() );
            assertEquals(900, cuenta.getSaldo().intValue() );
            assertEquals("900.12345", cuenta.getSaldo().toPlainString() );
        }

        @Test
        void testCreditoCuenta()
        {
            cuenta.credito( new BigDecimal(100) );
            assertNotNull( cuenta.getSaldo() );
            assertEquals(1100, cuenta.getSaldo().intValue() );
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString() );
        }

        @Test
        @Tag("banco")
        void testTransferirDineroCuentas()
        {
            Cuenta c1 = new Cuenta("Jhon Doe", new BigDecimal( "2500") );
            Cuenta c2 = new Cuenta("Andres", new BigDecimal( "1500.8989") );
            Banco banco = new Banco();
            banco.setNombre( "Banco del Estado" );
            banco.transferir( c2, c1, new BigDecimal( 500 ));
            assertEquals( "1000.8989", c2.getSaldo().toPlainString());
            assertEquals( "3000", c1.getSaldo().toPlainString());
        }
    }


    @Test
    @Tag("cuenta")
    @Tag("error")
    void testDineroInsuficienteException()
    {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
           cuenta.debito( new BigDecimal(1500) );
        });
        String actual = exception.getMessage();
        String esperado = "Dinero insuficiente";
        assertEquals( actual,esperado);
    }

    @Test
    //@Disabled
    @Tag("cuenta")
    @Tag("banco")
    @DisplayName("Probando relacion bidireccional de cuentas y banco con AssertAll")
    void testRelacionBancoCuentas()
    {
        //fail();
        Cuenta c1 = new Cuenta("Jhon Doe", new BigDecimal( "2500") );
        Cuenta c2 = new Cuenta("Andres", new BigDecimal( "1500.8989") );
        Banco banco = new Banco();
        banco.addCuenta( c1 );
        banco.addCuenta( c2 );
        banco.setNombre( "Banco del Estado" );
        banco.transferir( c2, c1, new BigDecimal( 500 ));
        assertAll(()->{assertEquals( "1000.8989", c2.getSaldo().toPlainString(),
                        ()->"El valor del saldo de la cuenta no es el esperado");},
                ()->{assertEquals( "3000", c1.getSaldo().toPlainString(),
                        ()->"El valor del saldo de la cuenta no es el esperado");},
                ()->{assertEquals( 2, banco.getCuentas().size(),
                        ()->"El numero de cuentas en el banco no es el esperado");},
                ()->{assertEquals("Banco del Estado", c1.getBanco().getNombre(),
                        ()->"El nombre del banco no es el correcto");},
                ()->{assertEquals("Andres", banco.getCuentas().stream()
                        .filter( c -> c.getPersona().equals("Andres") )
                        .findFirst()
                        .get().getPersona());},
                ()->{assertTrue(banco.getCuentas().stream()
                        .anyMatch(c -> c.getPersona().equals("Jhon Doe") ) );});
    }

    @Nested
    class SistemaOperativoTest
    {

        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
            System.out.println("Se ejecuta solo en Windows");
        }
        @Test
        @EnabledOnOs({OS.MAC, OS.WINDOWS})
        void testSoloLinuxMac() {
            System.out.println("Se ejecuta solo en Linux o Mac");
        }
    }

    @Nested
    class JavaVersionTest
    {

        @Test
        @EnabledOnJre( JRE.JAVA_8 )
        void soloJdk8() {
        }
        @Test
        @EnabledOnJre( JRE.JAVA_16 )
        void soloJdk16() {
        }

        @Test
        @DisabledOnJre( JRE.JAVA_16 )
        void testNoJdk16() {
        }

    }
    @Nested
    class SystemPropertiesTest
    {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach( (k, v) -> System.out.println( k + " : " + v) );
        }

        @Test
        @EnabledIfSystemProperty( named = "java.version", matches = "15.*")
        void testSystemProperty() {
        }

        @Test
        @DisabledIfSystemProperty( named = "os.arch", matches = ".*32.*")
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty( named = "user.name", matches = "vmorales")
        void testUsername() {
        }

        @Test
        @EnabledIfSystemProperty( named = "ENV", matches = "dev")
        void testDev() {
        }
    }
    @Nested
    class VariableAmbienteTest
    {

        @Test
        void imprimirVariablesAmbiente() {
            Map<String, String> properties = System.getenv();
            properties.forEach( (k, v) -> System.out.println(k + " : " + v));
        }

        @Test
        @EnabledIfEnvironmentVariable( named = "HOME", matches = "/Users/vmorales")
        void testHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable( named = "NUMBER_OF_PROCESSORS", matches= "12")
        void testProcesadores() {
        }

        @Test
        @EnabledIfEnvironmentVariable( named= "ENVIRONMENT", matches = "dev")
        void testEnv() {
        }
        @Test
        @DisabledIfEnvironmentVariable( named= "ENVIRONMENT", matches = "prod")
        void testEnvProdDisabled() {
        }
    }



    @Test
    @DisplayName("test saldo cuenta dev")
    void testSaldoCuentaDev()
    {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev); // <- Si no es correcto, se salta lo demas pero no falla
        assertNotNull( cuenta.getSaldo() );
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("test saldo cuenta dev 2")
    void testSaldoCuentaDev2()
    {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(true, () -> {
            assertNotNull( cuenta.getSaldo() );
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

    }

    @Tag("param")
    @Nested
    class PruebasParametrizadas
    {
        @ParameterizedTest( name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "700", "1000"})
        void testDebitoCuentaValueSource(String monto)
        {
            cuenta.debito( new BigDecimal( monto ) );
            assertNotNull( cuenta.getSaldo() );
            assertTrue( cuenta.getSaldo().compareTo( BigDecimal.ZERO) > 0 );
        }

        @ParameterizedTest( name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000"})
        void testDebitoCuentaCsv(String index, String monto)
        {
            System.out.println( index + " : " + monto);
            cuenta.debito( new BigDecimal( monto ) );
            assertNotNull( cuenta.getSaldo() );
            assertTrue( cuenta.getSaldo().compareTo( BigDecimal.ZERO) > 0 );
        }

        @ParameterizedTest( name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,John,Andres", "250,200,Pepe, Pepe", "300,300, maria, Maria",
                "510,500,Lucas, Luca", "750,700, Cata, Cata", "1000.12345,1000.12345, Arturo, Artur"})
        void testDebitoCuentaCsv2(String saldo, String monto, String esperado, String actual)
        {
            System.out.println( saldo + "->" + monto);
            cuenta.setSaldo( new BigDecimal(saldo) );
            cuenta.debito( new BigDecimal( monto ) );
            cuenta.setPersona(actual);
            assertNotNull( cuenta.getSaldo() );
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue( cuenta.getSaldo().compareTo( BigDecimal.ZERO) > 0 );
        }

        @ParameterizedTest( name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource( resources = "/data.csv")
        void testDebitoCuentaCsvFileSource( String monto)
        {
            System.out.println( monto);
            cuenta.debito( new BigDecimal( monto ) );
            assertNotNull( cuenta.getSaldo() );
            assertTrue( cuenta.getSaldo().compareTo( BigDecimal.ZERO) >= 0 );
        }

        @ParameterizedTest( name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource( resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual)
        {
            System.out.println( monto);
            cuenta.debito( new BigDecimal( monto ) );
            cuenta.setPersona(actual);
            assertNotNull( cuenta.getSaldo() );
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue( cuenta.getSaldo().compareTo( BigDecimal.ZERO) > 0 );
        }
    }
    @RepeatedTest(value = 5, name= "Repeticion numero {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info)
    {
        if( info.getCurrentRepetition() == 3)
        {
            System.out.println( "Estamos en la repeticion "+ info.getCurrentRepetition() );
        }
        cuenta.debito( new BigDecimal(100) );
        assertNotNull( cuenta.getSaldo() );
        assertEquals(900, cuenta.getSaldo().intValue() );
        assertEquals("900.12345", cuenta.getSaldo().toPlainString() );
    }


    @Tag("param")
    @ParameterizedTest( name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource( "montoList" )
    void testDebitoCuentaMethodSource( String monto)
    {
        System.out.println( monto);
        cuenta.debito( new BigDecimal( monto ) );
        assertNotNull( cuenta.getSaldo() );
        assertTrue( cuenta.getSaldo().compareTo( BigDecimal.ZERO) > 0 );
    }
    static List<String> montoList()
    {
        return Arrays.asList("100", "200", "300", "500", "700", "1000");
    }
    @Nested
    @Tag("timeout")
    class EjemploTimeout
    {
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.SECONDS.sleep(1);
        }

        @Test
        @Timeout( value=1000, unit = TimeUnit.MILLISECONDS )
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(1000);
        }

        @Test
        void testAssertionTimeout() {
            assertTimeout(Duration.ofSeconds(5), ()->{
                TimeUnit.MILLISECONDS.sleep(5000);
            });
        }
    }


}