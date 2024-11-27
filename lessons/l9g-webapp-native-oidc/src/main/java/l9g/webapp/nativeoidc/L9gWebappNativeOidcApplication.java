
/*
 * Copyright 2024 Thorsten Ludewig (t.ludewig@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * The main class for the L9gWebappNativeOidcApplication.
 * This is the entry point for the Spring Boot application.
 *
 * <p>
 * This application is configured to use Spring Boot's auto-configuration
 * feature, which simplifies the setup of the application by automatically
 * configuring Spring and third-party libraries based on the dependencies
 * present on the classpath.</p>
 *
 * <p>
 * To run the application, use the {@link SpringApplication#run(Class, String...)}
 * method, passing in this class and the command-line arguments.</p>
 *
 * <p>
 * Example usage:</p>
 * <pre>
 * {@code
 * public static void main(String[] args) {
 *     SpringApplication.run(L9gWebappNativeOidcApplication.class, args);
 * }
 * }
 * </pre>
 *
 * <p>
 * Make sure to configure the application properties as needed in the
 * application.properties or application.yml file.</p>
 *
 * @see SpringApplication
 * @see SpringBootApplication
 */
package l9g.webapp.nativeoidc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class L9gWebappNativeOidcApplication
{
  public static void main(String[] args)
  {
    SpringApplication.run(L9gWebappNativeOidcApplication.class, args);
  }

}
