public final class GlobalUtils
{
    private GlobalUtils()
    {
        
    }
    
    public static final boolean epsilonEquals(float f1, float f2)
    {
        return epsilonEquals(f1, f2, GlobalConstants.FLOATING_POINT_EPSILON);
    }
    
    public static final boolean epsilonEquals(float f1, float f2, float epsilon)
    {
        return Math.abs(f2 - f1) < epsilon;
    }
}