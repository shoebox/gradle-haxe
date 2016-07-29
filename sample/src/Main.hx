package;

class Main
{
	public function new()
	{
		trace("constructor");
		#if desktop
			trace("desktop");
		#elseif mobile
			trace("mobile");
		#elseif tablet
			trace("tablet");
		#end
	}

	public static function main()
	{
		new Main();
	}
}
