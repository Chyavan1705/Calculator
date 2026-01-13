package com.example.calculator;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

public class Calculator extends JFrame {

    boolean darkTheme = true;

    public Calculator() {

        setSize(320,360);
        setLayout(new BorderLayout(4,4));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Calculator");
        setResizable(false);
        
        setIconImage(
        	    new ImageIcon(
        	        Calculator.class.getResource("/com/example/calculator/keys.png")
        	    ).getImage()
        	);

        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(300,60));
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setFont(new Font("Monospaced",Font.BOLD,26));
        field.setEditable(false);

        this.add(field,BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5,4,4,4));
        panel.setBorder(new EmptyBorder(6,6,6,6));

        String[] buttons = {
            "CE","<=","^","/",
            "1","2","3","x",
            "4","5","6","-",
            "7","8","9","+",
            "Switch","0",".","="
        };

        JButton[] btns = new JButton[buttons.length];

        for(int i=0;i<buttons.length;i++) {
            JButton btn = new JButton(buttons[i]);
            btn.setFont(new Font("Monospaced",Font.BOLD,14));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setForeground(Color.BLACK);
            btn.setBorder(new LineBorder(new Color(80,80,80)));

            if(buttons[i].equals("Switch")) {
                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        darkTheme = !darkTheme;
                        applyTheme(field,panel,btns,buttons);
                    }
                });
            }
            else {
                btn.addActionListener(e -> handler(e, field));
            }

            btns[i] = btn;
            panel.add(btn);
        }

        this.add(panel,BorderLayout.CENTER);
        applyTheme(field,panel,btns,buttons);
        setVisible(true);
    }

    void handler(ActionEvent e, JTextField field) {

        String btntype = ((JButton)e.getSource()).getText();
        if(field.getText().equals("Error")) {
        	field.setText("");
        }

        switch(btntype) {
            case "CE":
                field.setText("");
                break;
            case "<=":            	
                if(!field.getText().isEmpty()) {
                    field.setText(field.getText().substring(0, field.getText().length()-1));
                }
                break;
            case "=":
                try {
                    String exp = field.getText().replace("x","*");
                    double result = evaluate(exp);

                    if(result == (long) result) {
                        field.setText((long)result + "");
                    }
                    else {
                        field.setText(String.format("%.3f", result));
                    }
                    
                    field.setText(result+"");
                }
                catch(Exception ex) {
                    field.setText("Error");
                }
                break;
            default:
                field.setText(field.getText() + btntype);
        }
    }

    int precedence(char op) {
        if(op=='+' || op=='-') return 1;
        if(op=='*' || op=='/') return 2;
        if(op=='^') return 3;

        return 0;
    }

    double applyOp(double b, double a, char op) {

        if(op=='+') return a+b;
        if(op=='-') return a-b;
        if(op=='*') return a*b;
        if(op=='/') return a/b;
        if(op=='^') return Math.pow(a,b);

        return 0;
    }

    double evaluate(String exp) {
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for(int i=0;i<exp.length();i++) {

            char ch = exp.charAt(i);

            if(ch==' ') continue;

            if((ch>='0' && ch<='9') || ch=='.') {

                String num = "";

                while(i<exp.length() &&
                     ((exp.charAt(i)>='0' && exp.charAt(i)<='9') || exp.charAt(i)=='.')) {

                    num += exp.charAt(i);
                    i++;
                }

                i--;
                values.push(Double.parseDouble(num));
            }          
            else {

                while(!ops.isEmpty() && precedence(ops.peek()) >= precedence(ch)) {
                    values.push(applyOp(values.pop(), values.pop(), ops.pop()));
                }

                ops.push(ch);
            }
        }
        while(!ops.isEmpty()) {
            values.push(applyOp(values.pop(), values.pop(), ops.pop()));
        }

        return values.pop();
    }

    void applyTheme(JTextField field, JPanel panel, JButton[] btns, String[] buttons) {
        if(darkTheme) {

            getContentPane().setBackground(new Color(90,90,90));
            panel.setBackground(new Color(70,70,70));
            field.setBackground(new Color(160,160,160));
            field.setBorder(new BevelBorder(BevelBorder.LOWERED));

            for(int i=0;i<btns.length;i++) {
                if("+-x/=%^<=".contains(buttons[i])) {
                    btns[i].setBackground(new Color(120,120,120));
                } else {
                    btns[i].setBackground(new Color(100,100,100));
                }
            }
        }
        else {

            getContentPane().setBackground(new Color(180,180,180));
            panel.setBackground(new Color(160,160,160));
            field.setBackground(new Color(230,230,230));
            field.setBorder(new BevelBorder(BevelBorder.LOWERED));

            for(int i=0;i<btns.length;i++) {
                if("+-x/=%^<=".contains(buttons[i])) {
                    btns[i].setBackground(new Color(200,200,200));
                } else {
                    btns[i].setBackground(new Color(220,220,220));
                }
            }
        }
    }

    public static void main(String[] args) {
        new Calculator();
    }
}
