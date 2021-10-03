# JUnit 5
- JUnit platform: Core donde se encuentra el test engine
    - Jupiter: Versión 5
    - Vintage: Contiene las versiones JUnit 3 y 4

- Anotaciones incluidas:
    - @Test : Indica que es una prueba que se tiene que ejecutar en el Runner

    - @RepeatedTest

    - @ParametrizedTest

    - @DisplayName: Permite modificar el nombre que se muestra al ejecutar las pruebas

    - @Nested: Permite crear clases anidadas para organizar de mejor manera las pruebas dentro de una misma clase principal. Se pued usar @BeforeEach y @AfterEach

    - @Tag: Permite ejecutar pruebas de forma selectiva

    - @ExtendWith

    - @TestInstance(TestInstance.Lifecycle.[PER_CLASS | PER_METHOD]): Nos permite modificar la forma de trabajar del ciclo de vida, haciendo que todas las pruebas sean ejecutadas en instancias independientes (valor por default) o usando la misma instancia para todas las pruebas. NO es recomendable cambiar el comportamiento del ciclo de vida porque se puede generar dependencia entre las pruebas.

    - @BeforeEach: Se ejecuta antes de cada prueba unitaria.

    - @AfterEach: Se ejecuta después de cada prueba unitaria.
        - Si el ciclo de vida es el de defecto, el método debe ser de clase ( static )
        - Si el ciclo de vida es PER_CLASS, el método puede ser de instancia.

    - @BeforeAll: Se ejecuta una sola vez antes de todas las pruebas unitarias.
        - Si el ciclo de vida es el de defecto, el método debe ser de clase ( static )
        - Si el ciclo de vida es PER_CLASS, el método puede ser de instancia.

    - @AfterAll: Se ejecuta una sola vez al final de todas las pruebas unitarias.

    - @Disabled: Indica que se salte esa prueba

    - @DisabledOnOs( OS.WINDOWS | OS.MAC | OS.LINUX | OS.OTHER | OS.SOLARIS | OS.AIX)

    - @EnabledOnOs( OS.WINDOWS | OS.MAC | OS.LINUX | OS.OTHER | OS.SOLARIS | OS.AIX)

    -RepeatedTest
    
# Assertions
Sirve para afirmar el resultado esperado por la prueba unitaria.
- assertNotNull
- assertNull
- assertEquals
- assertTrue
- assertFalse
- assertAll
# Assumptions
Sirve para evaluar una condición de forma programática. Podemos habilitar o deshabilitar un bloque de pruebas si se cumple una condición. Sirve  para asumir algo, en caso de que la suposicion no sea correcta, solo se ignora el resto de la prueba pero no se considera un error.
~~~
import static org.junit.api.Assumptions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;
~~~

- assumeTrue
- assumeFalse
- assumingThat
# Fail
Con fail() se obliga a que falle la prueba donde está siendo invocado.

