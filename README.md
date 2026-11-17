# Round-Robin Load Balancing with Spring Boot API Gateway

This is a runnable three-part Spring Boot demo:

1. **Discovery Server (Eureka)** — `http://localhost:8761`
2. **Driver Service** — start the same executable JAR three times. Each process uses `server.port=0`, so the OS assigns a different dynamic port. All register as `DRIVER-SERVICE`.
3. **API Gateway** — `http://localhost:8080`, routing `/drivers/**` to `lb://DRIVER-SERVICE`.

Spring Cloud LoadBalancer selects registered service instances using its default round-robin algorithm.

## Requirements

- JDK 17 or newer
- Apache Maven 3.8.6 or newer
- Internet connection on the first build (Maven downloads dependencies)

Check:
```bash
java -version
mvn -version
```

## Run on Windows

Open a terminal in the extracted `round-robin-api-gateway` folder and run:
```bat
mvn clean package
```
Then open **four** terminal windows, all in the project root.

**Terminal 1 — Eureka:**
```bat
java -jar discovery-server	arget\discovery-server-1.0.0.jar
```

**Terminals 2, 3 and 4 — three independent driver instances:**
```bat
java -jar driver-service	arget\driver-service-1.0.0.jar --server.port=0
```
Run that command once in each of the three terminals. Each process will print a different randomly assigned port.

**Terminal 5 — Gateway:**
```bat
java -jar api-gateway	argetpi-gateway-1.0.0.jar
```

## Run on Linux / macOS

From the extracted project root:
```bash
mvn clean package
java -jar discovery-server/target/discovery-server-1.0.0.jar
```
In three other terminals, run once per terminal:
```bash
java -jar driver-service/target/driver-service-1.0.0.jar --server.port=0
```
In another terminal:
```bash
java -jar api-gateway/target/api-gateway-1.0.0.jar
```

## Verify service registration

Open `http://localhost:8761` in a browser. Under **Instances currently registered with Eureka**, wait until three `DRIVER-SERVICE` instances appear. It can take about 10–30 seconds after starting the services.

## Verify load distribution

Send repeated requests through the gateway (not directly to the driver ports).

**Windows PowerShell:**
```powershell
1..12 | ForEach-Object { Invoke-RestMethod http://localhost:8080/drivers }
```

**Windows CMD:**
```bat
for /L %i in (1,1,12) do @curl -s http://localhost:8080/drivers
```

**Linux / macOS:**
```bash
for i in {1..12}; do curl -s http://localhost:8080/drivers; echo; done
```

Each response contains the actual `instancePort` used by that running process. You should see responses coming from all three different ports. With three healthy instances, the default round-robin strategy generally cycles across them; exact first instance/order is not guaranteed. The port field makes distribution visible.

Example response:
```json
{
  "message": "Driver service response",
  "service": "DRIVER-SERVICE",
  "instancePort": "53142",
  "instanceId": "set-by-start-command"
}
```
Your actual dynamic ports will differ. Check the startup logs for each driver process to identify its assigned port.

## Important notes / troubleshooting

- Start Eureka first, then all three driver processes, then the gateway.
- If the gateway starts before the driver instances register, wait a short time and retry.
- If a request returns `503 Service Unavailable`, check that all three driver processes are running and registered in Eureka.
- Ensure ports `8761` and `8080` are free. Driver ports are assigned dynamically.
- All three driver processes use the same service name `DRIVER-SERVICE`; do not change it in only one place.
- The route is configured exactly as requested: `Path=/drivers/**` and `uri: lb://DRIVER-SERVICE`.
- This demo uses in-memory Eureka discovery and is intended for learning/local testing, not production deployment.
