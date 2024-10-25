import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShamirSecret {
    static class Point {
        BigInteger x;
        BigInteger y;

        Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    private static BigInteger convertFromBase(String number, int base) {
        return new BigInteger(number, base);
    }

    private static List<Point> parsePoints(JsonObject json, int k) {
        List<Point> points = new ArrayList<>();
        int count = 0;

        for (Map.Entry<String, com.google.gson.JsonElement> entry : json.entrySet()) {
            if (entry.getKey().equals("keys")) continue;

            JsonObject pointData = entry.getValue().getAsJsonObject();
            String value = pointData.get("value").getAsString();
            int base = Integer.parseInt(pointData.get("base").getAsString());

            // Print decoded values for verification
            System.out.println("Point " + entry.getKey() + ": Base " + base + ", Value " + value);
            
            BigInteger x = new BigInteger(entry.getKey());
            BigInteger y = convertFromBase(value, base);
            
            System.out.println("Decoded y value: " + y);

            points.add(new Point(x, y));
            count++;

            if (count >= k) break;
        }

        return points;
    }

    private static BigInteger lagrangeInterpolation(List<Point> points) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger term = points.get(i).y;
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    numerator = numerator.multiply(BigInteger.ZERO.subtract(points.get(j).x));
                    denominator = denominator.multiply(points.get(i).x.subtract(points.get(j).x));
                }
            }

            term = term.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }

    public static void main(String[] args) {
        try {
            Gson gson = new Gson();
            
            // First test case
            System.out.println("\nProcessing Test Case 1:");
            String testCase1Path = "src/main/resources/testcase1.json";
            JsonObject json1 = gson.fromJson(new FileReader(testCase1Path), JsonObject.class);
            int k1 = json1.getAsJsonObject("keys").get("k").getAsInt();
            System.out.println("k value: " + k1);
            List<Point> points1 = parsePoints(json1, k1);
            BigInteger secret1 = lagrangeInterpolation(points1);
            
            // Second test case
            System.out.println("\nProcessing Test Case 2:");
            String testCase2Path = "src/main/resources/testcase2.json";
            JsonObject json2 = gson.fromJson(new FileReader(testCase2Path), JsonObject.class);
            int k2 = json2.getAsJsonObject("keys").get("k").getAsInt();
            System.out.println("k value: " + k2);
            List<Point> points2 = parsePoints(json2, k2);
            BigInteger secret2 = lagrangeInterpolation(points2);
            
            System.out.println("\nResults:");
            System.out.println("Secret for test case 1: " + secret1);
            System.out.println("Secret for test case 2: " + secret2);
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error processing data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}