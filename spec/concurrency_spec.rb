require 'certmaker_common'
require 'timeout'
require 'uri'
require 'json'
require 'net/http'

describe 'Concurrency Tests' do
  include CertmakerMethods

  # something bad happened if a test takes more than six seconds
  around(:each) do |test|
    Timeout::timeout(6) {
      test.run
    }
  end

  before(:all) do
    print "Creating certmaker process... "
    @certmaker_proc = create_certmaker()
    puts " pid is #{@certmaker_proc.pid}"
  end

  after(:all) do
    #do any cleanup
    Process.kill('INT', @certmaker_proc.pid)
    puts "Waiting for forked procs to terminate.."
    Process.waitall()
    print "done"
  end

  def get(url_str)
    uri = URI.parse(url_str)
    client = Net::HTTP.new uri.host, uri.port
    return client.request(Net::HTTP::Get.new(uri.request_uri))
  end

  it 'check the ping page' do
    resp = get('http://localhost:8080/ping')
    resp.code.should == '200'
  end

  it 'request a product cert' do
    resp = get('http://localhost:8080/cert/single?product=69')
    resp.code.should == '200'
  end

  it 'request ten product certs with the same rhic' do
    threads = []
    10.times do |i|
        threads[i] = Thread.new{
            response = get("http://localhost:8080/cert/concurrency-test?product=69")
            result = JSON.parse(response.body)
            Thread.current["key"] = result['certificates'].first['key']
            Thread.current["serial"] = result['certificates'].first['serial']['id']
        }
    end
    keylist = []
    seriallist = []
    threads.each {|t| 
                t.join
                keylist.push(t["key"])
                seriallist.push(t["serial"])
            }
    keylist.uniq.length.should == 1
    seriallist.uniq.length.should == 10
  end
end
