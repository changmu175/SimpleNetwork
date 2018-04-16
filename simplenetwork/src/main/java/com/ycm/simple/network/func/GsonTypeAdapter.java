package com.ycm.simple.network.func;

import android.util.Log;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Created by changmuyu on 2018/4/12.
 * Description:
 */

public class GsonTypeAdapter {
    public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else if (peek == JsonToken.STRING) {
                return Boolean.parseBoolean(in.nextString());
            }
            return in.nextBoolean();
        }
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value);
        }
    };

    public static final TypeAdapter<Integer> INTEGER = new TypeAdapter<Integer>() {
        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            try {
                if (value == null) {
                    value = 0;
                }
                out.value(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Integer read(JsonReader in) {
            try {
                Integer value;
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    Log.e("TypeAdapter", "null is not a number");
                    return 0;
                }
                if (in.peek() == JsonToken.BOOLEAN) {
                    boolean b = in.nextBoolean();
                    Log.e("TypeAdapter", b + " is not a number");
                    return 0;
                }
                if (in.peek() == JsonToken.STRING) {
                    String str = in.nextString();
                    if (isInt(str)) {
                        return Integer.parseInt(str);
                    } else {
                        Log.e("TypeAdapter", str + " is not a int number");
                        return 0;
                    }
                } else {
                    value = in.nextInt();
                    return value;
                }
            } catch (Exception e) {
                Log.e("TypeAdapter", "Not a number", e);
            }
            return 0;
        }
    };

    public static final TypeAdapter<Byte> BYTE = new TypeAdapter<Byte>() {
        @Override
        public void write(JsonWriter out, Byte value) {
            try {
                if (value == null) {
                    value = 0;
                }
                out.value(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Byte read(JsonReader in) {
            try {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    Log.e("TypeAdapter", "null is not a number");
                    return 0;
                }
                if (in.peek() == JsonToken.BOOLEAN) {
                    boolean b = in.nextBoolean();
                    Log.e("TypeAdapter", b + " is not a number");
                    return 0;
                }
                if (in.peek() == JsonToken.STRING) {
                    String str = in.nextString();
                    if (isInt(str)) {
                        return Byte.valueOf(str);
                    } else {
                        Log.e("TypeAdapter", str + " is not a number");
                        return 0;
                    }
                } else {
                    Integer value = in.nextInt();
                    return (byte) (value == null ? 0 : value);
                }
            } catch (NumberFormatException e) {
                Log.e("TypeAdapter", e.getMessage(), e);
            } catch (Exception e) {
                Log.e("TypeAdapter", e.getMessage(), e);
            }
            return 0;
        }
    };

    public static final TypeAdapter<Float> FLOAT = new TypeAdapter<Float>() {
        @Override
        public void write(JsonWriter out, Float value) {
            try {
                if (value == null) {
                    value = 0F;
                }
                out.value(value.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Float read(JsonReader in) {
            try {
                Float value;
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    Log.e("TypeAdapter", "null is not a number");
                    return 0F;
                }
                if (in.peek() == JsonToken.BOOLEAN) {
                    boolean b = in.nextBoolean();
                    Log.e("TypeAdapter", b + " is not a number");
                    return 0F;
                }
                if (in.peek() == JsonToken.STRING) {
                    String str = in.nextString();
                    if (isFloat(str)) {
                        return Float.parseFloat(str);
                    } else {
                        Log.e("TypeAdapter", str + " is not a number");
                        return 0F;
                    }
                } else {
                    String str = in.nextString();
                    value = Float.valueOf(str);
                }
                return value;
            } catch (Exception e) {
                Log.e("TypeAdapter", "Not a number", e);
            }
            return 0F;
        }

    };

    public static final TypeAdapter<Double> DOUBLE = new TypeAdapter<Double>() {
        @Override
        public void write(JsonWriter out, Double value) {
            try {
                if (value == null) {
                    value = 0D;
                }
                out.value(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Double read(JsonReader in) {
            try {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    Log.e("TypeAdapter", "null is not a number");
                    return 0D;
                }
                if (in.peek() == JsonToken.BOOLEAN) {
                    boolean b = in.nextBoolean();
                    Log.e("TypeAdapter", b + " is not a number");
                    return 0D;
                }
                if (in.peek() == JsonToken.STRING) {
                    String str = in.nextString();
                    if (isFloat(str)) {
                        return Double.parseDouble(str);
                    } else {
                        Log.e("TypeAdapter", str + " is not a number");
                        return 0D;
                    }
                } else {
                    Double value = in.nextDouble();
                    return value == null ? 0D : value;
                }
            } catch (NumberFormatException e) {
                Log.e("TypeAdapter", e.getMessage(), e);
            } catch (Exception e) {
                Log.e("TypeAdapter", e.getMessage(), e);
            }
            return 0D;
        }
    };

    public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>() {
        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            try {
                if (value == null) {
                    value = 0L;
                }
                out.value(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public Long read(JsonReader in) {
            try {
                Long value;
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    Log.e("TypeAdapter", "null is not a number");
                    return 0L;
                }
                if (in.peek() == JsonToken.BOOLEAN) {
                    boolean b = in.nextBoolean();
                    Log.e("TypeAdapter", b + " is not a number");
                    return 0L;
                }
                if (in.peek() == JsonToken.STRING) {
                    String str = in.nextString();
                    if (isInt(str)) {
                        return Long.parseLong(str);
                    } else {
                        Log.e("TypeAdapter", str + " is not a int number");
                        return 0L;
                    }
                } else {
                    value = in.nextLong();
                    return value;
                }
            } catch (Exception e) {
                Log.e("TypeAdapter", "Not a number", e);
            }
            return 0L;
        }
    };

    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        @Override
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        @Override
        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    };

    public static final TypeAdapter<BigDecimal> BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
        @Override
        public BigDecimal read(JsonReader in) throws IOException {
            try {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    Log.e("TypeAdapter", "null is not a number");
                    return BigDecimal.valueOf(0);
                }
                if (in.peek() == JsonToken.BOOLEAN) {
                    boolean b = in.nextBoolean();
                    Log.e("TypeAdapter", b + " is not a number");
                    return BigDecimal.valueOf(0);
                }
                if (in.peek() == JsonToken.STRING) {
                    String str = in.nextString();
                    if (isInt(str)) {
                        return new BigDecimal(str);
                    } else {
                        Log.e("TypeAdapter", str + " is not a number");
                        return BigDecimal.valueOf(0);
                    }
                }
            } catch (NumberFormatException e) {
                Log.e("TypeAdapter", e.getMessage(), e);
            } catch (Exception e) {
                Log.e("TypeAdapter", e.getMessage(), e);
            }
            return BigDecimal.valueOf(0);
        }

        @Override
        public void write(JsonWriter out, BigDecimal value) throws IOException {
            out.value(value);
        }
    };

    public static boolean isInt(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isFloat(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
}
