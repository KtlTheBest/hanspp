
public abstract class Data
{
   public static final int BOOL = 1;
   public static final int CHAR = 2;
   public static final int INTEGER = 3;
   public static final int DOUBLE = 4;
   public static final int PTR = 5;

   public static final int OTHER = 6;

   
   int sel;  // This is for quick selection by switch.

   bool getbool( ) { throw new java.lang.Error( "Data is not bool" );
   char getchar( )
   int getinteger( )
   bool getdouble( )
   bool getptr( )

   
   
