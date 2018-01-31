public class Radar {

    /**
     *  To test leftLaneDetect we need a radar.
     */
    private double value1;
    private double value2;

    /**
     * Updated to have contain 2 values, due to TC2 leftLeftDetect
     * @return
     */
    public double[] getValues() {
        double[] values = {value1, value2};
        return values;
    }

    public void setValues(double value1, double value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

}
