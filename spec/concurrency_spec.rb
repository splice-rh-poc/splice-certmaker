require 'certmaker_common'
require 'timeout'
require 'uri'
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


  it 'request a product cert' do
    resp = get('http://localhost:8080/foo?product=69')
    resp.code.should == '200'
  end

  it 'request two product certs with the same rhic' do
    resp = get('http://localhost:8080/foo?product=69')
    resp.code.should == '200'
  end
end
