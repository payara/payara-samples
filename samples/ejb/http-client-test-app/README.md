# EJB over HTTP Client Test Application

## Usage
1. Create a `test.jar` file containing the classes of `model` and `server` package (e.g. simple _jar_ export).
2. Deploy the created `test.jar` in the application server using the admin console. The application should be named `test`.
3. Run the `Client` as normal Java application. You should get value output.
  If exceptions are printed the communication is broken.
