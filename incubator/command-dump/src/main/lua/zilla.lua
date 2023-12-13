--[[

    Copyright 2021-2023 Aklivity Inc

    Licensed under the Aklivity Community License (the "License"); you may not use
    this file except in compliance with the License.  You may obtain a copy of the
    License at

      https://www.aklivity.io/aklivity-community-license/

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OF ANY KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations under the License.

]]
zilla_protocol = Proto("Zilla", "Zilla Frames")

HEADER_OFFSET = 0
LABELS_OFFSET = 16

BEGIN_ID = 0x00000001
DATA_ID = 0x00000002
END_ID = 0x00000003
ABORT_ID = 0x00000004
FLUSH_ID = 0x00000005
RESET_ID = 0x40000001
WINDOW_ID = 0x40000002
SIGNAL_ID = 0x40000003
CHALLENGE_ID = 0x40000004

AMQP_ID = 0x112dc182
GRPC_ID = 0xf9c7583a
HTTP_ID = 0x8ab62046
KAFKA_ID = 0x084b20e1
MQTT_ID = 0xd0d41a76
PROXY_ID = 0x8dcea850
TLS_ID = 0x99f321bc

local flags_types = {
    [0] = "Not set",
    [1] = "Set"
}

local ext_proxy_address_family_types = {
    [0] = "INET",
    [1] = "INET4",
    [2] = "INET6",
    [3] = "UNIX",
    [4] = "NONE",
}

local ext_proxy_address_protocol_types = {
    [0] = "STREAM",
    [1] = "DATAGRAM",
}

local ext_proxy_info_types = {
    [0x01] = "ALPN",
    [0x02] = "AUTHORITY",
    [0x05] = "IDENTITY",
    [0x20] = "SECURE",
    [0x30] = "NAMESPACE",
}

local ext_proxy_secure_info_types = {
    [0x21] = "VERSION",
    [0x22] = "NAME",
    [0x23] = "CIPHER",
    [0x24] = "SIGNATURE",
    [0x25] = "KEY",
}

local fields = {
    -- header
    frame_type_id = ProtoField.uint32("zilla.frame_type_id", "Frame Type ID", base.HEX),
    frame_type = ProtoField.string("zilla.frame_type", "Frame Type", base.NONE),
    protocol_type_id = ProtoField.uint32("zilla.protocol_type_id", "Protocol Type ID", base.HEX),
    protocol_type = ProtoField.string("zilla.protocol_type", "Protocol Type", base.NONE),
    stream_type_id = ProtoField.uint32("zilla.stream_type_id", "Stream Type ID", base.HEX),
    stream_type = ProtoField.string("zilla.stream_type", "Stream Type", base.NONE),
    core = ProtoField.uint32("zilla.core", "Core", base.DEC),
    offset = ProtoField.uint32("zilla.offset", "Offset", base.HEX),
    core_offset = ProtoField.string("zilla.core_offset", "Core/Offset", base.NONE),

    -- labels
    origin_namespace = ProtoField.string("zilla.origin_namespace", "Origin Namespace", base.STRING),
    origin_binding = ProtoField.string("zilla.origin_binding", "Origin Binding", base.STRING),
    routed_namespace = ProtoField.string("zilla.routed_namespace", "Routed Namespace", base.STRING),
    routed_binding = ProtoField.string("zilla.routed_binding", "Routed Binding", base.STRING),

    -- all frames
    origin_id = ProtoField.uint64("zilla.origin_id", "Origin ID", base.HEX),
    routed_id = ProtoField.uint64("zilla.routed_id", "Routed ID", base.HEX),
    stream_id = ProtoField.uint64("zilla.stream_id", "Stream ID", base.HEX),
    direction = ProtoField.string("zilla.direction", "Direction", base.NONE),
    initial_id = ProtoField.uint64("zilla.initial_id", "Initial ID", base.HEX),
    reply_id = ProtoField.uint64("zilla.reply_id", "Reply ID", base.HEX),
    sequence = ProtoField.int64("zilla.sequence", "Sequence", base.DEC),
    acknowledge = ProtoField.int64("zilla.acknowledge", "Acknowledge", base.DEC),
    maximum = ProtoField.int32("zilla.maximum", "Maximum", base.DEC),
    timestamp = ProtoField.uint64("zilla.timestamp", "Timestamp", base.HEX),
    trace_id = ProtoField.uint64("zilla.trace_id", "Trace ID", base.HEX),
    authorization = ProtoField.uint64("zilla.authorization", "Authorization", base.HEX),

    -- begin frame
    affinity = ProtoField.uint64("zilla.affinity", "Affinity", base.HEX),

    -- data frame
    flags = ProtoField.uint8("zilla.flags", "Flags", base.HEX),
    flags_fin = ProtoField.uint8("zilla.flags_fin", "FIN", base.DEC, flags_types, 0x01),
    flags_init = ProtoField.uint8("zilla.flags_init", "INIT", base.DEC, flags_types, 0x02),
    flags_incomplete = ProtoField.uint8("zilla.flags_incomplete", "INCOMPLETE", base.DEC, flags_types, 0x04),
    flags_skip = ProtoField.uint8("zilla.flags_skip", "SKIP", base.DEC, flags_types, 0x08),
    budget_id = ProtoField.uint64("zilla.budget_id", "Budget ID", base.HEX),
    reserved = ProtoField.int32("zilla.reserved", "Reserved", base.DEC),
    length = ProtoField.int32("zilla.length", "Length", base.DEC),
    progress = ProtoField.int64("zilla.progress", "Progress", base.DEC),
    progress_maximum = ProtoField.string("zilla.progress_maximum", "Progress/Maximum", base.NONE),
    payload = ProtoField.protocol("zilla.payload", "Payload", base.HEX),

    -- window frame
    padding = ProtoField.int32("zilla.padding", "Padding", base.DEC),
    minimum = ProtoField.int32("zilla.minimum", "Minimum", base.DEC),
    capabilities = ProtoField.uint8("zilla.capabilities", "Capabilities", base.HEX),

    -- signal frame
    cancel_id = ProtoField.uint64("zilla.cancel_id", "Cancel ID", base.HEX),
    signal_id = ProtoField.int32("zilla.signal_id", "Signal ID", base.DEC),
    context_id = ProtoField.int32("zilla.context_id", "Context ID", base.DEC),

    -- proxy extension
        -- address
    ext_proxy_address_family = ProtoField.uint8("zilla.proxy_ext.address_family", "Family", base.DEC,
        ext_proxy_address_family_types),
    ext_proxy_address_protocol = ProtoField.uint8("zilla.proxy_ext.address_protocol", "Protocol", base.DEC,
        ext_proxy_address_protocol_types),
    ext_proxy_address_inet_source_port = ProtoField.uint16("zilla.proxy_ext.address_inet_source_port", "Source Port",
        base.DEC),
    ext_proxy_address_inet_destination_port = ProtoField.uint16("zilla.proxy_ext.address_inet_destination_port",
        "Destination Port", base.DEC),
    ext_proxy_address_inet_source = ProtoField.string("zilla.proxy_ext.address_inet_source", "Source", base.NONE),
    ext_proxy_address_inet_destination = ProtoField.string("zilla.proxy_ext.address_inet_destination", "Destination",
        base.NONE),
    ext_proxy_address_inet4_source = ProtoField.new("Source", "zilla.proxy_ext.address_inet4_source", ftypes.IPv4),
    ext_proxy_address_inet4_destination = ProtoField.new("Destination", "zilla.proxy_ext.address_inet4_destination",
        ftypes.IPv4),
    ext_proxy_address_inet6_source = ProtoField.new("Source", "zilla.proxy_ext.address_inet6_source", ftypes.IPv6),
    ext_proxy_address_inet6_destination = ProtoField.new("Destination", "zilla.proxy_ext.address_inet6_destination",
        ftypes.IPv6),
    ext_proxy_address_unix_source = ProtoField.string("zilla.proxy_ext.address_unix_source", "Source", base.NONE),
    ext_proxy_address_unix_destination = ProtoField.string("zilla.proxy_ext.address_unix_destination", "Destination",
        base.NONE),
        -- info
    ext_proxy_info_array_length = ProtoField.uint8("zilla.proxy_ext.info_array_length", "Length", base.DEC),
    ext_proxy_info_array_size = ProtoField.uint8("zilla.proxy_ext.info_array_size", "Size", base.DEC),
    ext_proxy_info_type = ProtoField.uint8("zilla.proxy_ext.info_type", "Type", base.HEX, ext_proxy_info_types),
    ext_proxy_info_length = ProtoField.uint16("zilla.proxy_ext.info_length", "Length", base.DEC),
    ext_proxy_info_alpn = ProtoField.string("zilla.proxy_ext.info_alpn", "Value", base.NONE),
    ext_proxy_info_authority = ProtoField.string("zilla.proxy_ext.info_authority", "Value", base.NONE),
    ext_proxy_info_identity = ProtoField.bytes("zilla.proxy_ext.info_identity", "Value", base.NONE),
    ext_proxy_info_namespace = ProtoField.string("zilla.proxy_ext.info_namespace", "Value", base.NONE),
    ext_proxy_info_secure = ProtoField.string("zilla.proxy_ext.info_secure", "Value", base.NONE),
    ext_proxy_info_secure_type = ProtoField.uint8("zilla.proxy_ext.info_secure_type", "Secure Type", base.HEX,
        ext_proxy_secure_info_types),
}

zilla_protocol.fields = fields;

function zilla_protocol.dissector(buffer, pinfo, tree)
    if buffer:len() == 0 then return end

    local subtree = tree:add(zilla_protocol, buffer(), "Zilla Frame")
    local slices = {}

    -- header
    slices.frame_type_id = buffer(HEADER_OFFSET, 4)
    local frame_type_id = slices.frame_type_id:le_uint()
    local frame_type = resolve_frame_type(frame_type_id)
    subtree:add_le(fields.frame_type_id, slices.frame_type_id)
    subtree:add(fields.frame_type, frame_type)

    slices.protocol_type_id = buffer(HEADER_OFFSET + 4, 4)
    local protocol_type_id = slices.protocol_type_id:le_uint()
    local protocol_type = resolve_type(protocol_type_id)
    subtree:add_le(fields.protocol_type_id, slices.protocol_type_id)
    subtree:add(fields.protocol_type, protocol_type)

    slices.core = buffer(HEADER_OFFSET + 8, 4)
    local core = slices.core:le_int()
    subtree:add_le(fields.core, slices.core)

    slices.offset = buffer(HEADER_OFFSET + 12, 4)
    local offset = slices.offset:le_int()
    subtree:add_le(fields.offset, slices.offset)

    local core_offset = string.format("%02d/%08x", core, offset)
    subtree:add(fields.core_offset, core_offset)

    -- labels
    slices.labels_length = buffer(LABELS_OFFSET, 4)
    local labels_length = slices.labels_length:le_uint()
    slices.labels = buffer(LABELS_OFFSET + 4, labels_length)

    -- origin id
    local frame_offset = LABELS_OFFSET + labels_length
    slices.origin_id = buffer(frame_offset + 4, 8)
    subtree:add_le(fields.origin_id, slices.origin_id)

    local label_offset = LABELS_OFFSET + 4;
    local origin_namespace_length = buffer(label_offset, 4):le_uint()
    label_offset = label_offset + 4
    slices.origin_namespace = buffer(label_offset, origin_namespace_length)
    label_offset = label_offset + origin_namespace_length
    if (origin_namespace_length > 0) then
        subtree:add(fields.origin_namespace, slices.origin_namespace)
    end

    local origin_binding_length = buffer(label_offset, 4):le_uint()
    label_offset = label_offset + 4
    slices.origin_binding = buffer(label_offset, origin_binding_length)
    label_offset = label_offset + origin_binding_length
    if (origin_binding_length > 0) then
        subtree:add(fields.origin_binding, slices.origin_binding)
    end

    -- routed id
    slices.routed_id = buffer(frame_offset + 12, 8)
    subtree:add_le(fields.routed_id, slices.routed_id)

    local routed_namespace_length = buffer(label_offset, 4):le_uint()
    label_offset = label_offset + 4
    slices.routed_namespace = buffer(label_offset, routed_namespace_length)
    label_offset = label_offset + routed_namespace_length
    if (routed_namespace_length > 0) then
        subtree:add(fields.routed_namespace, slices.routed_namespace)
    end

    local routed_binding_length = buffer(label_offset, 4):le_uint()
    label_offset = label_offset + 4
    slices.routed_binding = buffer(label_offset, routed_binding_length)
    label_offset = label_offset + routed_binding_length
    if (routed_binding_length > 0) then
        subtree:add(fields.routed_binding, slices.routed_binding)
    end

    -- stream id
    slices.stream_id = buffer(frame_offset + 20, 8)
    subtree:add_le(fields.stream_id, slices.stream_id)
    local stream_id = slices.stream_id:le_uint64();
    local direction
    local initial_id
    local reply_id
    if stream_id == UInt64(0) then
        direction = ""
    else
        if (stream_id % 2) == UInt64(0) then
            direction = "REP"
            initial_id = stream_id + UInt64(1)
            reply_id = stream_id
        else
            direction = "INI"
            initial_id = stream_id
            reply_id = stream_id - UInt64(1)
        end
        subtree:add(fields.initial_id, initial_id)
        subtree:add(fields.reply_id, reply_id)
    end
    subtree:add(fields.direction, direction)

    -- more frame properties
    slices.sequence = buffer(frame_offset + 28, 8)
    subtree:add_le(fields.sequence, slices.sequence)
    slices.acknowledge = buffer(frame_offset + 36, 8)
    subtree:add_le(fields.acknowledge, slices.acknowledge)
    slices.maximum = buffer(frame_offset + 44, 4)
    subtree:add_le(fields.maximum, slices.maximum)
    slices.timestamp = buffer(frame_offset + 48, 8)
    subtree:add_le(fields.timestamp, slices.timestamp)
    slices.trace_id = buffer(frame_offset + 56, 8)
    subtree:add_le(fields.trace_id, slices.trace_id)
    slices.authorization = buffer(frame_offset + 64, 8)
    subtree:add_le(fields.authorization, slices.authorization)

    pinfo.cols.protocol = zilla_protocol.name
    local info = "ZILLA " .. frame_type .. " " .. direction
    if protocol_type and protocol_type ~= "" then
        info = info .. " p=" .. protocol_type
    end
    pinfo.cols.info:set(info)

    -- begin
    if frame_type_id == BEGIN_ID then
        slices.affinity = buffer(frame_offset + 72, 8)
        subtree:add_le(fields.affinity, slices.affinity)
        handle_extension(buffer, slices, subtree, pinfo, info, frame_offset + 80)
    end

    -- data
    if frame_type_id == DATA_ID then
        slices.flags = buffer(frame_offset + 72, 1)
        local flags_label = string.format("Flags: 0x%02x", slices.flags:le_uint())
        local flags_subtree = subtree:add(zilla_protocol, buffer(), flags_label)
        flags_subtree:add_le(fields.flags_fin, slices.flags)
        flags_subtree:add_le(fields.flags_init, slices.flags)
        flags_subtree:add_le(fields.flags_incomplete, slices.flags)
        flags_subtree:add_le(fields.flags_skip, slices.flags)
        slices.budget_id = buffer(frame_offset + 73, 8)
        subtree:add_le(fields.budget_id, slices.budget_id)
        slices.reserved = buffer(frame_offset + 81, 4)
        subtree:add_le(fields.reserved, slices.reserved)

        local sequence = slices.sequence:le_int64();
        local acknowledge = slices.acknowledge:le_int64();
        local maximum = slices.maximum:le_int();
        local reserved = slices.reserved:le_int();
        local progress = sequence - acknowledge + reserved;
        local progress_maximum = progress .. "/" .. maximum
        subtree:add(fields.progress, progress)
        subtree:add(fields.progress_maximum, progress_maximum)
        pinfo.cols.info:set(info .. " [" .. progress_maximum .. "]")

        local payload_subtree = subtree:add(zilla_protocol, buffer(), "Payload")
        slices.length = buffer(frame_offset + 85, 4)
        local length = slices.length:le_int()
        slices.payload = buffer(frame_offset + 89, length)
        payload_subtree:add_le(fields.length, slices.length)
        payload_subtree:add(fields.payload, slices.payload)
        handle_extension(buffer, slices, subtree, pinfo, info, frame_offset + 89 + length)

        local dissector = resolve_dissector(protocol_type, slices.payload:tvb())
        if dissector then
            dissector:call(slices.payload:tvb(), pinfo, tree)
        end
    end

    -- end
    if frame_type_id == END_ID then
        handle_extension(buffer, slices, subtree, pinfo, info, frame_offset + 72)
    end

    -- abort
    if frame_type_id == ABORT_ID then
        handle_extension(buffer, slices, subtree, pinfo, info, frame_offset + 72)
    end

    -- flush
    if frame_type_id == FLUSH_ID then
        slices.budget_id = buffer(frame_offset + 72, 8)
        subtree:add_le(fields.budget_id, slices.budget_id)
        slices.reserved = buffer(frame_offset + 80, 4)
        subtree:add_le(fields.reserved, slices.reserved)
        handle_extension(buffer, slices, subtree, pinfo, info, frame_offset + 84)
    end

    -- reset
    if frame_type_id == RESET_ID then
        handle_extension(buffer, slices, subtree, pinfo, info, frame_offset + 72)
    end

    -- window
    if frame_type_id == WINDOW_ID then
        slices.budget_id = buffer(frame_offset + 72, 8)
        subtree:add_le(fields.budget_id, slices.budget_id)
        slices.padding = buffer(frame_offset + 80, 4)
        subtree:add_le(fields.padding, slices.padding)
        slices.minimum = buffer(frame_offset + 84, 4)
        subtree:add_le(fields.minimum, slices.minimum)
        slices.capabilities = buffer(frame_offset + 88, 1)
        subtree:add_le(fields.capabilities, slices.capabilities)

        local sequence = slices.sequence:le_int64();
        local acknowledge = slices.acknowledge:le_int64();
        local maximum = slices.maximum:le_int();
        local progress = sequence - acknowledge;
        local progress_maximum = progress .. "/" .. maximum
        subtree:add(fields.progress, progress)
        subtree:add(fields.progress_maximum, progress_maximum)

        pinfo.cols.info:set(info .. " [" .. progress_maximum .. "]")
    end

    -- signal
    if frame_type_id == SIGNAL_ID then
        slices.cancel_id = buffer(frame_offset + 72, 8)
        subtree:add_le(fields.cancel_id, slices.cancel_id)
        slices.signal_id = buffer(frame_offset + 80, 4)
        subtree:add_le(fields.signal_id, slices.signal_id)
        slices.context_id = buffer(frame_offset + 84, 4)
        subtree:add_le(fields.context_id, slices.context_id)

        local payload_subtree = subtree:add(zilla_protocol, buffer(), "Payload")
        slices.length = buffer(frame_offset + 88, 4)
        local length = slices.length:le_int()
        slices.payload = buffer(frame_offset + 92, length)
        payload_subtree:add_le(fields.length, slices.length)
        payload_subtree:add(fields.payload, slices.payload)
    end

    -- challenge
    if frame_type_id == CHALLENGE_ID then
        handle_extension(buffer, slices, subtree, pinfo, info, frame_offset + 72)
    end
end

function resolve_frame_type(frame_type_id)
    local frame_type = ""
        if frame_type_id == BEGIN_ID     then frame_type = "BEGIN"
    elseif frame_type_id == DATA_ID      then frame_type = "DATA"
    elseif frame_type_id == END_ID       then frame_type = "END"
    elseif frame_type_id == ABORT_ID     then frame_type = "ABORT"
    elseif frame_type_id == FLUSH_ID     then frame_type = "FLUSH"
    elseif frame_type_id == RESET_ID     then frame_type = "RESET"
    elseif frame_type_id == WINDOW_ID    then frame_type = "WINDOW"
    elseif frame_type_id == SIGNAL_ID    then frame_type = "SIGNAL"
    elseif frame_type_id == CHALLENGE_ID then frame_type = "CHALLENGE"
    end
    return frame_type
end

function handle_extension(buffer, slices, subtree, pinfo, info, offset)
    if buffer:len() > offset then
        slices.stream_type_id = buffer(offset, 4)
        local stream_type_id = slices.stream_type_id:le_uint();
        local stream_type = resolve_type(stream_type_id)
        local extensionLabel = string.format("Extension: %s", stream_type)
        slices.extension = buffer(offset)
        local extension_subtree = subtree:add(zilla_protocol, slices.extension, extensionLabel)
        extension_subtree:add(fields.stream_type_id, slices.stream_type_id)
        extension_subtree:add(fields.stream_type, stream_type)

        if stream_type_id == PROXY_ID then
            handle_proxy_extension(buffer, slices, extension_subtree, offset + 4)
        end

        if stream_type and stream_type ~= "" then
            pinfo.cols.info:set(info .. " s=" .. stream_type)
        end
    end
end

function handle_proxy_extension(buffer, slices, extension_subtree, offset)
    slices.ext_proxy_address_family = buffer(offset, 1)
    local address_family_id = slices.ext_proxy_address_family:le_int()
    local address_family = ext_proxy_address_family_types[address_family_id]
    local address_subtree_label = string.format("Address: %s", address_family)
    local info_offset
    if address_family == "INET" then
        slices.ext_proxy_address_protocol = buffer(offset + 1, 1)
        local source_length = buffer(offset + 2, 2):le_int()
        slices.ext_proxy_address_inet_source = buffer(offset + 4, source_length)
        local destination_length = buffer(offset + 4 + source_length, 2):le_int()
        slices.ext_proxy_address_inet_destination = buffer(offset + 6 + source_length, destination_length)
        local length = 6 + source_length + destination_length
        local address_subtree = extension_subtree:add(zilla_protocol, buffer(offset, length + 1), address_subtree_label)
        address_subtree:add(fields.ext_proxy_address_family, slices.ext_proxy_address_family)
        address_subtree:add(fields.ext_proxy_address_protocol, slices.ext_proxy_address_protocol)
        address_subtree:add(fields.ext_proxy_address_inet_source, slices.ext_proxy_address_inet_source)
        address_subtree:add(fields.ext_proxy_address_inet_destination, slices.ext_proxy_address_inet_destination)
        info_offset = offset + length + 1
    elseif address_family == "INET4" then
        slices.ext_proxy_address_protocol = buffer(offset + 1, 1)
        slices.ext_proxy_inet4_source = buffer(offset + 2, 4)
        slices.ext_proxy_inet4_destination = buffer(offset + 6, 4)
        slices.ext_proxy_inet_source_port = buffer(offset + 10, 2)
        slices.ext_proxy_inet_destination_port = buffer(offset + 12, 2)
        local length = 13;
        local address_subtree = extension_subtree:add(zilla_protocol, buffer(offset, length + 1), address_subtree_label)
        address_subtree:add(fields.ext_proxy_address_family, slices.ext_proxy_address_family)
        address_subtree:add(fields.ext_proxy_address_protocol, slices.ext_proxy_address_protocol)
        address_subtree:add(fields.ext_proxy_address_inet4_source, slices.ext_proxy_inet4_source)
        address_subtree:add(fields.ext_proxy_address_inet4_destination, slices.ext_proxy_inet4_destination)
        address_subtree:add_le(fields.ext_proxy_address_inet_source_port, slices.ext_proxy_inet_source_port)
        address_subtree:add_le(fields.ext_proxy_address_inet_destination_port, slices.ext_proxy_inet_destination_port)
        info_offset = offset + length + 1
    elseif address_family == "INET6" then
        slices.ext_proxy_address_protocol = buffer(offset + 1, 1)
        slices.ext_proxy_inet6_source = buffer(offset + 2, 16)
        slices.ext_proxy_inet6_destination = buffer(offset + 18, 16)
        slices.ext_proxy_inet_source_port = buffer(offset + 34, 2)
        slices.ext_proxy_inet_destination_port = buffer(offset + 36, 2)
        local length = 37;
        local address_subtree = extension_subtree:add(zilla_protocol, buffer(offset, length + 1), address_subtree_label)
        address_subtree:add(fields.ext_proxy_address_family, slices.ext_proxy_address_family)
        address_subtree:add(fields.ext_proxy_address_protocol, slices.ext_proxy_address_protocol)
        address_subtree:add(fields.ext_proxy_address_inet6_source, slices.ext_proxy_inet6_source)
        address_subtree:add(fields.ext_proxy_address_inet6_destination, slices.ext_proxy_inet6_destination)
        address_subtree:add_le(fields.ext_proxy_address_inet_source_port, slices.ext_proxy_inet_source_port)
        address_subtree:add_le(fields.ext_proxy_address_inet_destination_port, slices.ext_proxy_inet_destination_port)
        info_offset = offset + length + 1;
    elseif address_family == "UNIX" then
        local slice_address_protocol = buffer(offset + 1, 1)
        local slice_source = buffer(offset + 2, 108)
        local slice_destination = buffer(offset + 110, 108)
        local length = 217
        local address_subtree = extension_subtree:add(zilla_protocol, buffer(offset, length + 1), address_subtree_label)
        address_subtree:add(fields.ext_proxy_address_family, slices.ext_proxy_address_family)
        address_subtree:add(fields.ext_proxy_address_protocol, slice_address_protocol)
        address_subtree:add(fields.ext_proxy_address_unix_source, slice_source)
        address_subtree:add(fields.ext_proxy_address_unix_destination, slice_destination)
        info_offset = offset + length + 1
    elseif address_family == "NONE" then
        local length = 0
        local address_subtree = extension_subtree:add(zilla_protocol, buffer(offset, length + 1), address_subtree_label)
        address_subtree:add(fields.ext_proxy_address_family, slices.ext_proxy_address_family)
        info_offset = offset + 1
    end

    local slice_info_array_length = buffer(info_offset, 4)
    local slice_info_array_size = buffer(info_offset + 4, 4)
    local info_array_length = slice_info_array_length:le_int()
    local info_array_size = slice_info_array_size:le_int()
    local length = 8
    local label = string.format("Info (%d items)", info_array_size)
    local info_array_subtree = extension_subtree:add(zilla_protocol, buffer(info_offset, length), label)
    info_array_subtree:add_le(fields.ext_proxy_info_array_length, slice_info_array_length)
    info_array_subtree:add_le(fields.ext_proxy_info_array_size, slice_info_array_size)
    local item_offset = info_offset + length
    for i = 1, info_array_size do
        local slice_type_id = buffer(item_offset, 1)
        local type_id = slice_type_id:le_int()
        local type = ext_proxy_info_types[type_id]
        local label_format = "Info: %s: %s"
        item_offset = item_offset + 1
        if type == "ALPN" then
            local item_length, slice_length, slice_text = dissect_length_value(buffer, item_offset, 1)
            add_string_as_subtree(buffer(item_offset - 1, item_length + 1), extension_subtree, label_format, slice_type_id,
                slice_length, slice_text, fields.ext_proxy_info_type, fields.ext_proxy_info_length, fields.ext_proxy_info_alpn)
            item_offset = item_offset + item_length
        elseif type == "AUTHORITY" then
            local item_length, slice_length, slice_text = dissect_length_value(buffer, item_offset, 2)
            add_string_as_subtree(buffer(item_offset - 1, item_length + 1), extension_subtree, label_format, slice_type_id,
                slice_length, slice_text, fields.ext_proxy_info_type, fields.ext_proxy_info_length, fields.ext_proxy_info_authority)
            item_offset = item_offset + item_length
        elseif type == "IDENTITY" then
            local item_length, slice_length, slice_bytes = dissect_length_value(buffer, item_offset, 2)
            local label = string.format("Info: %s: 0x%s", type, slice_bytes:bytes())
            local subtree = extension_subtree:add(zilla_protocol, buffer(item_offset - 1, item_length + 1), label)
            subtree:add(fields.ext_proxy_info_type, slice_type_id)
            subtree:add_le(fields.ext_proxy_info_length, slice_length)
            subtree:add(fields.ext_proxy_info_identity, slice_bytes)
            item_offset = item_offset + item_length
        elseif type == "SECURE" then
            local slice_secure_type_id = buffer(item_offset, 1)
            local secure_type_id = slice_secure_type_id:le_int();
            local secure_type = ext_proxy_secure_info_types[secure_type_id]
            item_offset = item_offset + 1
            local length_length
            if secure_type == "VERSION" or secure_type == "CIPHER" or secure_type == "SIGNATURE" or secure_type == "KEY" then
                length_length = 1
            elseif secure_type == "NAME" then
                length_length = 2
            end
            local item_length, slice_length, slice_text = dissect_length_value(buffer, item_offset, length_length)
            local label = string.format("Info: %s: %s: %s", type, secure_type, slice_text:string())
            local subtree = extension_subtree:add(zilla_protocol, buffer(item_offset - 1, item_length + 1), label)
            subtree:add(fields.ext_proxy_info_type, slice_type_id)
            subtree:add(fields.ext_proxy_info_secure_type, slice_secure_type_id)
            subtree:add_le(fields.ext_proxy_info_length, slice_length)
            subtree:add(fields.ext_proxy_info_secure, slice_text)
            item_offset = item_offset + item_length
        elseif type == "NAMESPACE" then
            local item_length, slice_length, slice_text = dissect_length_value(buffer, item_offset, 2)
            add_string_as_subtree(buffer(item_offset - 1, item_length + 1), extension_subtree, label_format, slice_type_id, slice_length,
                slice_text, fields.ext_proxy_info_type, fields.ext_proxy_info_length, fields.ext_proxy_info_namespace)
            item_offset = item_offset + item_length
        end
    end
end

function dissect_length_value(buffer, item_offset, length_length)
    local slice_length = buffer(item_offset, length_length)
    local length = slice_length:le_int()
    local slice_value = buffer(item_offset + length_length, length)
    local item_length = length + length_length
    return item_length, slice_length, slice_value
end

function add_string_as_subtree(buffer, tree, label_format, slice_type_id, slice_length, slice_text, field_type, field_length,
        field_text)
    local type_id = slice_type_id:le_int()
    local type = ext_proxy_info_types[type_id]
    local text = slice_text:string()
    local label = string.format(label_format, type, text)
    local subtree = tree:add(zilla_protocol, buffer, label)
    subtree:add(field_type, slice_type_id)
    subtree:add_le(field_length, slice_length)
    subtree:add(field_text, slice_text)
end

function resolve_type(type_id)
    local type = ""
        if type_id == AMQP_ID  then type = "amqp"
    elseif type_id == GRPC_ID  then type = "grpc"
    elseif type_id == HTTP_ID  then type = "http"
    elseif type_id == KAFKA_ID then type = "kafka"
    elseif type_id == MQTT_ID  then type = "mqtt"
    elseif type_id == PROXY_ID then type = "proxy"
    elseif type_id == TLS_ID   then type = "tls"
    end
    return type
end

function resolve_dissector(protocol_type, payload)
    local dissector
        if protocol_type == "amqp"  then dissector = Dissector.get("amqp")
    elseif protocol_type == "http"  then dissector = resolve_http_dissector(payload)
    elseif protocol_type == "kafka" then dissector = Dissector.get("kafka")
    elseif protocol_type == "mqtt"  then dissector = Dissector.get("mqtt")
    elseif protocol_type == "tls"   then dissector = Dissector.get("tls")
    end
    return dissector
end

function resolve_http_dissector(payload)
    if payload:range(0, 3):int() + 9 == payload:len() then
        return Dissector.get("http2")
    elseif payload:range(0, 3):string() == "PRI" then
        return Dissector.get("http2")
    elseif payload:range(0, 4):string() == "HTTP" then
        return Dissector.get("http")
    elseif payload:range(0, 3):string() == "GET" then
        return Dissector.get("http")
    elseif payload:range(0, 4):string() == "POST" then
        return Dissector.get("http")
    elseif payload:range(0, 3):string() == "PUT" then
        return Dissector.get("http")
    elseif payload:range(0, 6):string() == "DELETE" then
        return Dissector.get("http")
    elseif payload:range(0, 4):string() == "HEAD" then
        return Dissector.get("http")
    elseif payload:range(0, 7):string() == "OPTIONS" then
        return Dissector.get("http")
    elseif payload:range(0, 5):string() == "TRACE" then
        return Dissector.get("http")
    elseif payload:range(0, 7):string() == "CONNECT" then
        return Dissector.get("http")
    else
        return nil
    end
end

local data_dissector = DissectorTable.get("tcp.port")
data_dissector:add(7114, zilla_protocol)
