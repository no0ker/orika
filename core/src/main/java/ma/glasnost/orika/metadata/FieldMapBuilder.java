/*
 * Orika - simpler, better and faster Java bean mapping
 *
 * Copyright (C) 2011-2013 Orika authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ma.glasnost.orika.metadata;

/**
 * FieldMapBuilder is used in cooperation with the ClassMapBuilder fluent api to
 * configure the details of a given FieldMap instance. <br>
 * <br>
 * 
 * A FieldMapBuilder instance is obtained from a ClassMapBuilder instance via
 * the <code>fieldMap(...)</code> method; it may then be used to further
 * configure that field mapping. Finally, the <code>add()</code> is used to add
 * the associated field mapping to the containing ClassMapBuilder.
 * 
 * @param <A>
 * @param <B>
 */
public class FieldMapBuilder<A, B> {
    
    private final ClassMapBuilder<A, B> classMapBuilder;
    
    private Property aProperty;
    
    private Property bProperty;
    
    private Property aInverseProperty;
    
    private Property bInverseProperty;
    
    private String converterId;
    
    private MappingDirection mappingDirection = MappingDirection.BIDIRECTIONAL;
    
    private boolean excluded;
    
    private boolean byDefault;
    
    private Boolean sourceMappedOnNull;
    
    private Boolean destinationMappedOnNull;
    
    FieldMapBuilder(final ClassMapBuilder<A, B> classMapBuilder, final String a, final String b, final boolean byDefault,
            final Boolean sourceMappedOnNull, final Boolean destinationMappedOnNull) {
        
        this(classMapBuilder, a, b, classMapBuilder.getAType(), classMapBuilder.getBType(), byDefault, sourceMappedOnNull,
                destinationMappedOnNull);
    }
    
    /**
     * Creates a new FieldMapBuilder, with type overrides for the aType and
     * bType
     * 
     * @param classMapBuilder
     * @param a
     * @param b
     * @param aType
     * @param bType
     */
    FieldMapBuilder(final ClassMapBuilder<A, B> classMapBuilder, final String a, final String b, final Type<?> aType, final Type<?> bType,
            final boolean byDefault, final Boolean sourceMappedOnNull, final Boolean destinationMappedOnNull) {
        
        this(classMapBuilder, classMapBuilder.resolveProperty(aType, a), classMapBuilder.resolveProperty(bType, b), byDefault,
                sourceMappedOnNull, destinationMappedOnNull);
    }
    
    /**
     * Constructs a new FieldMapBuilder, specifying the properties' definitions
     * explicitly
     * 
     * @param classMapBuilder
     * @param a
     *            the 'a' property of this FieldMap
     * @param b
     *            the 'b' property of this FieldMap
     * @param elementMap
     *            a sub-element map to associate with this field map
     * @param byDefault
     *            whether this FieldMapBuilder was generated by default or not
     */
    FieldMapBuilder(final ClassMapBuilder<A, B> classMapBuilder, final Property a, final Property b, final boolean byDefault,
            final Boolean sourceMappedOnNull, final Boolean destinationMappedOnNull) {
        this.classMapBuilder = classMapBuilder;
        this.byDefault = byDefault;
        this.aProperty = a;
        this.bProperty = b;
        this.sourceMappedOnNull = sourceMappedOnNull;
        this.destinationMappedOnNull = destinationMappedOnNull;
    }
    
    /**
     * @param propertyName
     * @return
     */
    String[] splitAtRootProperty(final String propertyName) {
        String[] parts = propertyName.split("\\[", 2);
        if (parts.length > 1) {
            if (!parts[1].endsWith("]")) {
                throw new IllegalArgumentException("Property name '" + propertyName + "' is invalid");
            }
            parts[1] = parts[1].substring(0, parts[1].length() - 1);
        }
        return parts;
    }
    
    /**
     * Adds the FieldMap configured by this builder to it's containing
     * ClassMapBuilder; use this method to complete specifications on a given
     * field mapping and return to the containing builder.
     * 
     * @return the containing ClassMapBuilder instance
     */
    public ClassMapBuilder<A, B> add() {
        
        classMapBuilder.addFieldMap(toFieldMap());
        return classMapBuilder;
    }
    
    /**
     * @param aInverse
     * @return this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> aInverse(final String aInverse) {
        final Type<?> type = aProperty.isCollection() ? aProperty.getElementType() : aProperty.getType();
        aInverseProperty = classMapBuilder.resolveProperty(type, aInverse);
        
        return this;
    }
    
    /**
     * @param bInverse
     * @return this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> bInverse(final String bInverse) {
        final Type<?> type = bProperty.isCollection() ? bProperty.getElementType() : bProperty.getType();
        bInverseProperty = classMapBuilder.resolveProperty(type, bInverse);
        
        return this;
    }
    
    /**
     * @param sourceMappedOnNull
     *            true|false to indicate whether the source property of this
     *            field map should be set to null (when mapping in the reverse
     *            direction) if the destination property's value is null
     * 
     * @return this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> mapNullsInReverse(final boolean sourceMappedOnNull) {
        this.sourceMappedOnNull = sourceMappedOnNull;
        
        return this;
    }
    
    /**
     * @param destinationMappedOnNull
     *            true|false to indicate whether the destination property of
     *            this field map should be set to null (when mapping in the
     *            forward direction) if the source property's value is null
     * 
     * @return this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> mapNulls(final boolean destinationMappedOnNull) {
        this.destinationMappedOnNull = destinationMappedOnNull;
        
        return this;
    }
    
    private FieldMap toFieldMap() {
        return new FieldMap(aProperty, bProperty, aInverseProperty, bInverseProperty, mappingDirection, excluded, converterId, byDefault,
                sourceMappedOnNull, destinationMappedOnNull);
    }
    
    /**
     * Specify that the configured field mapping (property) should only be used
     * when mapping in the direction from A to B
     * 
     * @return a reference to this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> aToB() {
        
        mappingDirection = MappingDirection.A_TO_B;
        
        return this;
    }
    
    /**
     * Specify that the configured field mapping (property) should only be used
     * when mapping in the direction from B to A
     * 
     * @return a reference to this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> bToA() {
        mappingDirection = MappingDirection.B_TO_A;
        
        return this;
    }
    
    /**
     * Specify that the configured field mapping (property) should only be used
     * when mapping in the direction from A to B
     * 
     * @param direction the direction to be applied to this field map
     * 
     * @return a reference to this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> direction(MappingDirection direction) {
        
        mappingDirection = direction;
        
        return this;
    }
    
    /**
     * Specify that the converter (which was previously registered with the
     * specified id) should be applied to this specific field mapping.
     * 
     * @param id
     *            the id with which the converter to use was registered
     * @return a reference to this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> converter(final String id) {
        this.converterId = id;
        return this;
    }
    
    /**
     * Specify that the property should be excluded from mapping
     * 
     * @return a reference to this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> exclude() {
        excluded = true;
        return this;
    }
    
    /**
     * Specify element type for A side property<br>
     * <i>When dealing with legacy code, prior to Java 1.5 you can add element
     * type of collections, this is not required when using generics</i>
     * 
     * @param rawType the elementType of of the 'B' field
     * @return a reference to this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> aElementType(final Class<?> rawType) {
        return aElementType(TypeFactory.valueOf(rawType));
    }
    
    /**
     * Specify element type for A side property<br>
     * <i>When dealing with legacy code, prior to Java 1.5 you can add element
     * type of collections, this is not required when using generics</i>
     * 
     * @param elementType the elementType of of the 'B' field
     * @return a reference to this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> aElementType(final Type<?> elementType) {
        aProperty = new Property.Builder().merge(aProperty).elementType(elementType).build();
        return this;
    }
    
    /**
     * Specify element type for B side property<br>
     * <i>When dealing with legacy code, prior to Java 1.5 you can add element
     * type of collections, this is not required when using generics</i>
     * 
     * @param rawType the elementType of of the 'B' field
     * @return a reference to this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> bElementType(final Class<?> rawType) {
        return bElementType(TypeFactory.valueOf(rawType));
    }
    
    /**
     * Specify element type for B side property<br>
     * <i>When dealing with legacy code, prior to Java 1.5 you can add element
     * type of collections, this is not required when using generics</i>
     * @param elementType the elementType of the 'B' field
     * @return a reference to this FieldMapBuilder
     */
    public FieldMapBuilder<A, B> bElementType(final Type<?> elementType) {
        bProperty = new Property.Builder().merge(bProperty).elementType(elementType).build();
        return this;
    }
    
    /**
     * Creates a FieldMap for the Map keys from A to B
     * 
     * @param aType
     * @param bType
     * @return a reference to this FieldMapBuilder
     */
    public static FieldMap mapKeys(final Type<?> aType, final Type<?> bType) {
        
        Property aProperty = new Property.Builder().name("key").getter("getKey()").setter("setKey(%s)").type(aType).build(null);
        
        Property bProperty = aProperty.copy(bType);
        
        return new FieldMap(aProperty, bProperty, null, null, MappingDirection.A_TO_B, false, null, false, null, null);
    }
    
    /**
     * Creates a FieldMap for the Map values from A to B
     * 
     * @param aType
     * @param bType
     * @return a reference to this FieldMapBuilder
     */
    public static FieldMap mapValues(final Type<?> aType, final Type<?> bType) {
        
        Property aProperty = new Property.Builder().name("value").getter("getValue()").setter("setValue(%s)").type(aType).build(null);
        
        Property bProperty = aProperty.copy(bType);
        
        return new FieldMap(aProperty, bProperty, null, null, MappingDirection.A_TO_B, false, null, false, null, null);
    }
}
