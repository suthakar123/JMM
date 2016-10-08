#!/usr/bin/env ruby
# dump the data.bin part of an uploaded zip file
# in human-readable format
MAGIC_NUMBER_SENSOR_CHUNK_HEADER = 0xA9.chr
MAGIC_NUMBER_SENSOR_PACKET_NO_EVENT = 0xAA.chr
MAGIC_NUMBER_SENSOR_PACKET_WITH_EVENT = 0xAB.chr
MAGIC_NUMBER_EMPTY_SENSOR_PACKET = 0xAC.chr
MAGIC_NUMBER_STATUS_RECORD = 0xA6.chr
MAGIC_NUMBER_CALIBRATION_RECORD = 0xA7.chr
MAGIC_NUMBER_TRANSMIT_REQUEST = 0xA5.chr
MAGIC_NUMBER_ACKNOWLEDGE = 0xA4.chr
MAGIC_NUMBER_NACK = 0xA3.chr

STATUS_RECORD_LENGTH = 13
CALIBRATION_RECORD_LENGTH = 3
SENSOR_CHUNK_HEADER_LENGTH = 6
SENSOR_PACKET_LENGTH = 7

class String
  def hexDump
    self.each_byte.collect { |b| "%02x" % b }.join(' ')
  end
  def hexDumpWithChars
    "#{hexDump} [ #{inspect} ]"
  end

  if /^1\.8\./.match RUBY_VERSION 
    def ord
      self[0].to_i
    end
  end
end

status_record = nil
calibration_record = nil

# Byte   Bits   Description
#  0      0     event mark
#  0      1-7   0xAA    magic number
#  1      0-7   flex value bits 0-7
#  2      0-7   X accelerometer value bits 0-7
#  3      0-7   Y accelerometer value bits 0-7
#  4      0-7   Z accelerometer value bits 0-7
#  5      0-2   flex value bits 8-10
#  5      3     not used
#  5      4-7   X accelerometer value bits 8-11
#  6      0-3   Y accelerometer value bits 8-11
#  6      4-7   Z accelerometer value bits 8-11
def receiveSensorRecord(file, recnum)
  rec = file.read(SENSOR_PACKET_LENGTH)
  (magic,flexlo,xlo,ylo,zlo,xflexhi,yzhi) = rec.unpack("AC6")
  event = 0
  case magic
    when MAGIC_NUMBER_SENSOR_PACKET_WITH_EVENT
      event = 1
    when MAGIC_NUMBER_SENSOR_PACKET_NO_EVENT
    when MAGIC_NUMBER_EMPTY_SENSOR_PACKET
      return nil
    else
      raise "Bad magic #{magic.inspect}"
  end
  flex = flexlo + ((xflexhi & 0x0F) << 8)
  x = xlo + ((xflexhi & 0xF0) << 4)
  y = ylo + ((yzhi & 0x0F) << 8)
  z = (zlo + ((yzhi & 0x70) << 4)) << 1
  [ rec, { :flex => flex, :x => x, :y => y, :z => z, :event => event } ]
end

def parseFile(file)
  while !file.eof?
    fpos = file.tell
    magic = file.read(1)
    case magic
      when MAGIC_NUMBER_STATUS_RECORD
        printf("[pos %d] STATUS_RECORD\n", fpos)
        rec = magic + file.read(STATUS_RECORD_LENGTH - 1)
        (magic, protver, numSRLo, numSRHi, calRecSize, flexCal, battPwrup, battNow) =  rec.unpack("ACvCv4")
        puts rec.hexDump
        numSR = numSRLo + (numSRHi << 16)
        status_record = { :magic => magic,
          :protVer => protver,
          :numSR => numSR,
          :calRecSize => calRecSize,
          :flexCal => flexCal,
          :battPwrup => battPwrup,
          :battNow => battNow }
        p status_record

      when MAGIC_NUMBER_CALIBRATION_RECORD
        printf("[pos %d] CALIBRATION_RECORD\n", fpos)
        rec = magic + file.read(CALIBRATION_RECORD_LENGTH - 1)
        puts rec.hexDump
        (magic, len) = rec.unpack("Av")
        calibration_record = file.read(len)
        puts calibration_record.hexDumpWithChars

      when MAGIC_NUMBER_SENSOR_CHUNK_HEADER
        rec = magic + file.read(SENSOR_CHUNK_HEADER_LENGTH - 1)
        (magic, sslo, sshi, numSensorRecords) = rec.unpack("AvCv")
        startingRec = sslo + (sshi << 16)
        printf("[pos %d] CHUNK startRec=%d numRecs=%d\n", fpos, startingRec, numSensorRecords)
        puts rec.hexDump
        numSensorRecords.times do |r|
          recnum = r + startingRec
          (bytes, rec) = receiveSensorRecord(file, recnum)
          if rec
            puts "#{bytes.hexDump} #{rec.inspect}"
          else
            puts "Empty record #{r}"
          end
        end

      when MAGIC_NUMBER_SENSOR_PACKET_NO_EVENT
      when MAGIC_NUMBER_SENSOR_PACKET_WITH_EVENT
      when MAGIC_NUMBER_EMPTY_SENSOR_PACKET

      else
        $stderr.puts("bad magic #{magic.inspect} at position #{file.tell - 1}")
        exit 1
    end
  end
end

parseFile(File.open(ARGV[0], "rb"))
