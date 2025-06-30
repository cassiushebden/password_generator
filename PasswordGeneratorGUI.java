import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGeneratorGUI extends JFrame {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    private final SecureRandom random = new SecureRandom();

    private JTextField lengthField;
    private JCheckBox upperBox, lowerBox, digitBox, symbolBox;
    private JTextField outputField;
    private JLabel strengthLabel;

    public PasswordGeneratorGUI() {
        setTitle("🔐 Secure Password Generator");
        setSize(500, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(7, 1, 5, 5));
        getContentPane().setBackground(new Color(240, 248, 255));  // Light blue background

        // Font style
        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        // Length Panel
        JPanel lengthPanel = new JPanel();
        lengthPanel.setBackground(new Color(240, 248, 255));
        lengthPanel.add(new JLabel("Password Length:"));
        lengthField = new JTextField("12", 5);
        lengthField.setFont(font);
        lengthPanel.add(lengthField);
        add(lengthPanel);

        // Option checkboxes
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(new Color(240, 248, 255));
        upperBox = new JCheckBox("Uppercase", true);
        lowerBox = new JCheckBox("Lowercase", true);
        digitBox = new JCheckBox("Digits", true);
        symbolBox = new JCheckBox("Symbols", true);
        for (JCheckBox box : new JCheckBox[]{upperBox, lowerBox, digitBox, symbolBox}) {
            box.setBackground(new Color(240, 248, 255));
            box.setFont(font);
            optionsPanel.add(box);
        }
        add(optionsPanel);

        // Generate button
        JButton generateButton = new JButton("Generate Password");
        generateButton.setFont(font);
        generateButton.addActionListener(this::generatePassword);
        add(generateButton);

        // Output field
        JPanel outputPanel = new JPanel();
        outputPanel.setBackground(new Color(240, 248, 255));
        outputPanel.add(new JLabel("Generated Password:"));
        outputField = new JTextField(20);
        outputField.setFont(new Font("Consolas", Font.BOLD, 14));
        outputField.setEditable(false);
        outputPanel.add(outputField);
        add(outputPanel);

        // Strength label
        strengthLabel = new JLabel("Strength: ");
        strengthLabel.setFont(font);
        strengthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(strengthLabel);

        // Copy button
        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.setFont(font);
        copyButton.addActionListener(this::copyToClipboard);
        add(copyButton);
    }

    private void generatePassword(ActionEvent e) {
        try {
            int length = Integer.parseInt(lengthField.getText());
            if (length < 4) {
                JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long.");
                return;
            }

            boolean useUpper = upperBox.isSelected();
            boolean useLower = lowerBox.isSelected();
            boolean useDigits = digitBox.isSelected();
            boolean useSymbols = symbolBox.isSelected();

            if (!useUpper && !useLower && !useDigits && !useSymbols) {
                JOptionPane.showMessageDialog(this, "Select at least one character type.");
                return;
            }

            String password = generateSecurePassword(length, useUpper, useLower, useDigits, useSymbols);
            outputField.setText(password);
            strengthLabel.setText("Strength: " + getPasswordStrength(password));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for password length.");
        }
    }

    private void copyToClipboard(ActionEvent e) {
        String password = outputField.getText();
        if (!password.isEmpty()) {
            StringSelection selection = new StringSelection(password);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            JOptionPane.showMessageDialog(this, "Password copied to clipboard!");
        }
    }

    private String generateSecurePassword(int length, boolean useUpper, boolean useLower, boolean useDigits, boolean useSymbols) {
        StringBuilder allChars = new StringBuilder();
        List<Character> passwordChars = new ArrayList<>();

        if (useUpper) {
            allChars.append(UPPER);
            passwordChars.add(randomCharFrom(UPPER));
        }
        if (useLower) {
            allChars.append(LOWER);
            passwordChars.add(randomCharFrom(LOWER));
        }
        if (useDigits) {
            allChars.append(DIGITS);
            passwordChars.add(randomCharFrom(DIGITS));
        }
        if (useSymbols) {
            allChars.append(SYMBOLS);
            passwordChars.add(randomCharFrom(SYMBOLS));
        }

        while (passwordChars.size() < length) {
            passwordChars.add(randomCharFrom(allChars.toString()));
        }

        Collections.shuffle(passwordChars, random);
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    private char randomCharFrom(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    private String getPasswordStrength(String password) {
        int length = password.length();
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSymbol = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*");

        int score = 0;
        if (length >= 8) score++;
        if (length >= 12) score++;
        if (hasUpper) score++;
        if (hasLower) score++;
        if (hasDigit) score++;
        if (hasSymbol) score++;

        return switch (score) {
            case 6, 5 -> "Very Strong 🔒";
            case 4 -> "Strong ✅";
            case 3 -> "Medium ⚠️";
            default -> "Weak ❌";
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PasswordGeneratorGUI app = new PasswordGeneratorGUI();
            app.setVisible(true);
        });
    }
}
