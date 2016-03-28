package com.noctarius.borabora;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

final class StreamValue
        extends AbstractValue {

    private final Collection<SemanticTagProcessor> processors;
    private final Decoder stream;
    private final long index;
    private final long length;

    StreamValue(MajorType majorType, ValueType valueType, Decoder stream, long index, long length,
                Collection<SemanticTagProcessor> processors) {

        super(majorType, valueType);

        if (index == -1) {
            throw new IllegalArgumentException("No index available for CBOR type");
        }
        if (length == -1) {
            throw new IllegalArgumentException(String.format("Length calculation for CBOR type %s is not available", majorType));
        }

        this.stream = stream;
        this.index = index;
        this.length = length;
        this.processors = processors;
    }

    @Override
    public <V> V tag() {
        return extract(() -> matchMajorType(MajorType.SemanticTag), () -> tag0(index, length));
    }

    @Override
    public Number number() {
        return extract(() -> matchValueType(ValueTypes.Uint, ValueTypes.NInt, ValueTypes.Float),
                () -> stream.readNumber(valueType(), index));
    }

    @Override
    public Sequence sequence() {
        return extract(() -> matchValueType(ValueTypes.Sequence), () -> stream.readSequence(index, processors));
    }

    @Override
    public Dictionary dictionary() {
        return extract(() -> matchValueType(ValueTypes.Dictionary), () -> stream.readDictionary(index, processors));
    }

    @Override
    public String string() {
        return extract(this::matchStringValueType, () -> stream.readString(index));
    }

    @Override
    public Boolean bool() {
        return extract(() -> matchValueType(ValueTypes.Bool), () -> stream.getBooleanValue(index));
    }

    @Override
    public byte[] raw() {
        return extract(() -> stream.readRaw(index, length));
    }

    @Override
    protected <T> T extract(Validator validator, Supplier<T> supplier) {
        short head = stream.transientUint8(index);
        // Null is legal for all types
        if (stream.isNull(head)) {
            return null;
        }
        // Not null? Validate value type
        if (validator != null) {
            validator.validate();
        }
        // Semantic tag? Extract real value
        if (MajorType.SemanticTag == majorType()) {
            return tag0(index, length);
        }
        return supplier.get();
    }

    private <V> SemanticTagProcessor<V> findProcessor(long index) {
        Optional<SemanticTagProcessor> optional = processors.stream().filter(p -> p.handles(stream, index)).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    private <V> V tag0(long index, long length) {
        SemanticTagProcessor<V> processor = findProcessor(index);
        if (processor == null) {
            return null;
        }
        return processor.process(stream, index, length, processors);
    }

}
