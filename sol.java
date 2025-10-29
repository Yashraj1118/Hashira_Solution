import java.util.*;
import org.json.JSONObject;

public class HashiraAssignment {

    static long convertToDecimal(String value, int base) {
        long num = 0;
        for (char c : value.toCharArray()) {
            int digit;
            if (Character.isDigit(c)) digit = c - '0';
            else digit = 10 + (Character.toLowerCase(c) - 'a');
            num = num * base + digit;
        }
        return num;
    }

    static double[] solve(double[][] A, double[] b) {
        int n = A.length;

        for (int i = 0; i < n; i++) {
      
            int maxRow = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[maxRow][i])) {
                    maxRow = j;
                }
            }

       
            double[] temp = A[i];
            A[i] = A[maxRow];
            A[maxRow] = temp;

            double t = b[i];
            b[i] = b[maxRow];
            b[maxRow] = t;

            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
                b[j] -= factor * b[i];
            }
        }

        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            x[i] = b[i];
            for (int j = i + 1; j < n; j++) {
                x[i] -= A[i][j] * x[j];
            }
            x[i] /= A[i][i];
        }
        return x;
    }

    public static void main(String[] args) {

        String jsonInput = """
        {
            "keys": {
                "n": 4,
                "k": 3
            },
            "1": { "base": "10", "value": "4" },
            "2": { "base": "2", "value": "111" },
            "3": { "base": "10", "value": "12" },
            "6": { "base": "4", "value": "213" }
        }
        """;

        JSONObject obj = new JSONObject(jsonInput);
        JSONObject keys = obj.getJSONObject("keys");

        int n = keys.getInt("n");
        int k = keys.getInt("k");

        List<Double> x = new ArrayList<>();
        List<Double> y = new ArrayList<>();

        // Collect first k (x, y) pairs
        int count = 0;
        for (String key : obj.keySet()) {
            if (key.equals("keys")) continue;
            if (count >= k) break;
            JSONObject root = obj.getJSONObject(key);
            int base = Integer.parseInt(root.getString("base"));
            String value = root.getString("value");
            long decimal = convertToDecimal(value, base);
            x.add(Double.parseDouble(key)); 
            y.add((double) decimal);
            count++;
        }

        double[][] A = new double[k][k];
        double[] b = new double[k];

        for (int i = 0; i < k; i++) {
            b[i] = y.get(i);
            for (int j = 0; j < k; j++) {
                A[i][j] = Math.pow(x.get(i), j);
            }
        }

        double[] coeffs = solve(A, b);

        System.out.println("Polynomial coefficients (a0 + a1x + a2x² ...):");
        for (int i = 0; i < coeffs.length; i++) {
            System.out.printf("a%d = %.4f%n", i, coeffs[i]);
        }

        System.out.println("\nFinal Polynomial:");
        StringBuilder poly = new StringBuilder("y = ");
        for (int i = 0; i < coeffs.length; i++) {
            if (i > 0) poly.append(" + ");
            poly.append(String.format("%.4f", coeffs[i]));
            if (i > 0) poly.append("*x");
            if (i > 1) poly.append("^").append(i);
        }
        System.out.println(poly);
    }
}
