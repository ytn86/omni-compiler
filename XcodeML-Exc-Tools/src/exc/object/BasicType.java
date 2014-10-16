/* 
 * $TSUKUBA_Release: Omni OpenMP Compiler 3 $
 * $TSUKUBA_Copyright:
 *  PLEASE DESCRIBE LICENSE AGREEMENT HERE
 *  $
 */
package exc.object;

/**
 * Xtype object to present Basic type, such as int, char, ...
 */
public class BasicType extends Xtype
{
    public final static int UNDEF                   = 0;  // undefined type
    public final static int VOID                    = 1;  // void type
    public final static int BOOL                    = 2;  // C99 _Bool, Fortran logical
    public final static int CHAR                    = 3;  // signed character type
    public final static int UNSIGNED_CHAR           = 4;  // unsigned character type
    public final static int SHORT                   = 5;  // signed short
    public final static int UNSIGNED_SHORT          = 6;  // unsigned short
    public final static int INT                     = 7;  // signed int, Fortran integer
    public final static int UNSIGNED_INT            = 8;  // unsigned int
    public final static int LONG                    = 9;  // long int
    public final static int UNSIGNED_LONG           = 10; // unsigned long int
    public final static int LONGLONG                = 11; // longlong int
    public final static int UNSIGNED_LONGLONG       = 12; // unsigned longlong
    public final static int FLOAT                   = 13; // single precision floating point
    public final static int DOUBLE                  = 14; // double precision floating point, Fortran real/double
    public final static int LONG_DOUBLE             = 15; // extended precision floting point
    public final static int FLOAT_IMAGINARY         = 16; // C99 float _Imaginary
    public final static int DOUBLE_IMAGINARY        = 17; // C99 double _Imaginary
    public final static int LONG_DOUBLE_IMAGINARY   = 18; // C99 long double _Imaginary
    public final static int FLOAT_COMPLEX           = 19; // C99 float _Complex
    public final static int DOUBLE_COMPLEX          = 20; // C99 double _Complex, Fortran complex
    public final static int LONG_DOUBLE_COMPLEX     = 21; // C99 long double _Complex
    public final static int GCC_BUILTIN_VA_LIST     = 22; // GCC builtin_va_list
    public final static int F_CHARACTER             = 23; // Fortran character
    public final static int F_NUMERIC               = 24; // Fortran scalar numeric (integer/real/double)
    public final static int F_NUMERIC_ALL           = 25; // Fortran all numeric (integer/real/double/complex)
    
    /** member variable */
    private int basic_type;
    
    /** Fortran kind parameter */
    private Xobject fkind;
    
    /** Fortran len parameter */
    private Xobject flen;

    /** constructor */
    public BasicType(int basic_type, String id, int typeQualFlags, Xobject gccAttrs,
                     Xobject fkind, Xobject flen, Xobject[] codimensions)
    {
        super(Xtype.BASIC, id, typeQualFlags, gccAttrs, codimensions);
        this.basic_type = basic_type;
        this.fkind = fkind;
        this.flen = flen;
    }

    public BasicType(int basic_type)
    {
        this(basic_type, null, 0, null, null, null);
    }

    public BasicType(int basic_type, int typeQualFlags)
    {
        this(basic_type, null, typeQualFlags, null, null, null);
    }
    
    public BasicType(int basic_type, String id, int typeQualFlags, Xobject gccAttrs,
                     Xobject fkind, Xobject flen)
    {
        this(basic_type, id, typeQualFlags, gccAttrs, fkind, flen, null);
    }

    /** return basic type */
    @Override
    public int getBasicType()
    {
        return basic_type;
    }

    public final static TypeInfo getTypeInfo(int bt)
    {
        for(int i = 0; i < type_infos.length; ++i) {
            if(type_infos[i].type.getBasicType() == bt)
                return type_infos[i];
        }
        return null;
    }
    
    public final static TypeInfo getTypeInfoByName(String name)
    {
        for(int i = 0; i < type_infos.length; ++i) {
            if(name.equals(type_infos[i].cname) ||
                name.equals(type_infos[i].fname))
                return type_infos[i];
        }
        return null;
    }
    
    public final static TypeInfo getTypeInfoByCName(String name)
    {
        for(int i = 0; i < type_infos.length; ++i) {
            if(name.equals(type_infos[i].cname))
                return type_infos[i];
        }
        return null;
    }

    public final static TypeInfo getTypeInfoByFName(String name)
    {
        for(int i = 0; i < type_infos.length; ++i) {
            if(name.equals(type_infos[i].fname))
                return type_infos[i];
        }
        return null;
    }

    @Override
    public boolean isUnsigned()
    {
        switch(basic_type) {
        case UNSIGNED_CHAR:
        case UNSIGNED_SHORT:
        case UNSIGNED_INT:
        case UNSIGNED_LONG:
        case UNSIGNED_LONGLONG:
        case BOOL:
            return true;
        }
        return false;
    }

    @Override
    public boolean isIntegral()
    {
        return (basic_type >= CHAR) && (basic_type <= UNSIGNED_LONGLONG);
    }
    
    @Override
    public boolean isBool()
    {
        return (basic_type == BOOL);
    }

    @Override
    public boolean isFloating()
    {
        return (basic_type >= FLOAT) && (basic_type <= LONG_DOUBLE);
    }
    
    @Override
    public boolean isComplexOrImaginary()
    {
        return (basic_type >= FLOAT_COMPLEX) && (basic_type <= LONG_DOUBLE_COMPLEX);
    }
    
    @Override
    public boolean isVoid()
    {
        return (basic_type == VOID);
    }

    @Override
    public Xobject getFkind()
    {
        return fkind;
    }
    
    @Override
    public Xobject getFlen()
    {
        return flen;
    }
    
    public void setFlen(Xobject x)
    {
        flen = x;
    }

    @Override
    public boolean isFlenVariable()
    {
        return (flen != null && flen.Opcode() == Xcode.INT_CONSTANT &&
            flen.getInt() == -1);
    }
    
    public static Xtype Conversion(Xtype lt, Xtype rt)
    {
        int b1 = lt.getBasicType();
        int b2 = rt.getBasicType();
        if(b1 > b2)
            return getTypeInfo(b1).type;
        else
            return getTypeInfo(b2).type;
    }
    
    @Override
    public Xtype copy(String id)
    {
        BasicType type = new BasicType(basic_type, id, getTypeQualFlags(),
                                       getGccAttributes(), getFkind(), getFlen(),
                                       copyCodimensions());
        return type;
    }
}
