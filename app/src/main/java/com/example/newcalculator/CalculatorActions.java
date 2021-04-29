package com.example.newcalculator;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.Formatter;

public class CalculatorActions implements Parcelable {
    private double curArg;
    private double firstArg;
    private double secondArg;
    private char operation;

    private boolean isFractional;
    private int fractionalDigit;

    private State state;

    private int actionSelected;

    private enum State {
        FIRST_ARG_INPUT,
        SECOND_ARG_INPUT,
        RESULT_SHOW
    }

    public CalculatorActions() {
        state = State.FIRST_ARG_INPUT;
    }

    protected CalculatorActions(Parcel in) {
        curArg = in.readDouble();
        firstArg = in.readDouble();
        secondArg = in.readDouble();
        operation = (char) in.readInt();
        isFractional = in.readByte() != 0;
        fractionalDigit = in.readInt();
        actionSelected = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(curArg);
        dest.writeDouble(firstArg);
        dest.writeDouble(secondArg);
        dest.writeInt((int) operation);
        dest.writeByte((byte) (isFractional ? 1 : 0));
        dest.writeInt(fractionalDigit);
        dest.writeInt(actionSelected);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CalculatorActions> CREATOR = new Creator<CalculatorActions>() {
        @Override
        public CalculatorActions createFromParcel(Parcel in) {
            return new CalculatorActions(in);
        }

        @Override
        public CalculatorActions[] newArray(int size) {
            return new CalculatorActions[size];
        }
    };

    public void onNumPressed(int num) {
        if (state == State.RESULT_SHOW) {
            state = State.FIRST_ARG_INPUT;
            curArg = 0;
            firstArg = 0;
            secondArg = 0;
            isFractional = false;
            fractionalDigit = 0;
        }

        if (String.format("%." + fractionalDigit + "f", curArg).length() < 15) {
            if (num == 0) {
                if (curArg != 0) {
                    currentArgAdd(num);
                }
            } else {
                currentArgAdd(num);
            }
        }
    }

    public void currentArgAdd(double num) {
        if (!isFractional) {
            curArg = curArg * 10 + num;
        } else {
            fractionalDigit++;
            double decPow = Math.pow(10, fractionalDigit);

            curArg = curArg + num / decPow;

            BigDecimal decimal = new BigDecimal(String.valueOf(curArg));
            decimal = decimal.setScale(fractionalDigit, BigDecimal.ROUND_DOWN);
            curArg = decimal.doubleValue();
        }
    }

    public void onActionPressed(int actionId) {
        if (actionId == R.id.buttonClear) {
            pressedClear();
        } else if (actionId == R.id.buttonDot) {
            pressedDot();
        } else if (actionId == R.id.buttonToggleSign) {
            pressedToggleSign();
        } else if (actionId == R.id.buttonBackSpace) {
            pressedBackSpace();
        } else if (actionId == R.id.buttonEqual && state == State.SECOND_ARG_INPUT) {
            pressedEqual();
        } else if (curArg != 0) {
            if (state == State.SECOND_ARG_INPUT) {
                pressedEqual();
            }
            pressedOperation(actionId);
        }
    }

    public void pressedToggleSign() {
        curArg = -1 * curArg;
    }

    public void pressedEqual() {
        secondArg = curArg;
        state = State.RESULT_SHOW;
        curArg = 0;
        isFractional = false;
        fractionalDigit = 0;

        switch (actionSelected) {
            case R.id.buttonAdd:
                curArg = firstArg + secondArg;
                break;
            case R.id.buttonSub:
                curArg = firstArg - secondArg;
                break;
            case R.id.buttonMul:
                curArg = firstArg * secondArg;
                break;
            case R.id.buttonDiv:
                curArg = firstArg / secondArg;
                break;
            case R.id.buttonPercent:
                curArg = firstArg / 100 * secondArg;
        }

        fractionalDigit = findFractionalDigits(curArg);
        if (fractionalDigit > 0) {
            isFractional = true;
        }
        actionSelected = 0;
    }

    public void pressedOperation(int actionId) {

        state = State.SECOND_ARG_INPUT;
        firstArg = curArg;
        secondArg = 0;
        curArg = 0;
        isFractional = false;
        fractionalDigit = 0;

        switch (actionId) {
            case R.id.buttonAdd:
                actionSelected = R.id.buttonAdd;
                operation = '+';
                break;
            case R.id.buttonSub:
                actionSelected = R.id.buttonSub;
                operation = '-';
                break;
            case R.id.buttonMul:
                actionSelected = R.id.buttonMul;
                operation = '*';
                break;
            case R.id.buttonDiv:
                actionSelected = R.id.buttonDiv;
                operation = '/';
                break;
            case R.id.buttonPercent:
                actionSelected = R.id.buttonPercent;
                operation = '%';
                break;
        }
    }

    public void pressedClear() {
        state = State.FIRST_ARG_INPUT;
        curArg = 0;
        firstArg = 0;
        secondArg = 0;
        isFractional = false;
        fractionalDigit = 0;
    }

    public void pressedDot() {
        isFractional = true;
    }

    public void pressedBackSpace() {
        if (curArg != 0) {
            if (isFractional) {
                fractionalDigit--;
                if (fractionalDigit == 0) {
                    isFractional = false;
                }
            } else {
                curArg = curArg / 10;
            }

            BigDecimal decimal = new BigDecimal(String.valueOf(curArg));
            decimal = decimal.setScale(fractionalDigit, BigDecimal.ROUND_DOWN);
            curArg = decimal.doubleValue();
        }
    }

    public int findFractionalDigits(double num) {

        if (num - Math.round(num) == 0) {
            return 0;
        } else {
            BigDecimal decimal = new BigDecimal(String.valueOf(num));
            return decimal.scale();
        }
    }

    public String getText() {
        return String.format("%." + fractionalDigit + "f", curArg);
    }

    public String getOperation() {
        String result = "";
        Formatter f = new Formatter();

        if (state == State.SECOND_ARG_INPUT) {
            f.format("%s %c", String.valueOf(firstArg), operation);
            result = f.toString();
        }
        if (state == State.RESULT_SHOW) {
            f.format("%s %c %s =", String.valueOf(firstArg), operation, String.valueOf(secondArg));
            result = f.toString();
        }
        return result;
    }
}

