package ru.maksmerfy.testappformelon

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import ru.maksmerfy.testappformelon.model.Shop
import ru.maksmerfy.testappformelon.viewmodel.CityMallBrand
import ru.maksmerfy.testappformelon.viewmodel.Shops


class MainActivity : AppCompatActivity() {
    //Создадим свойства которые потребуются в будущем
    val shops: Shops = Shops()
    val cityMallBrand: CityMallBrand = CityMallBrand()
    val listCitys: MutableList<String> = mutableListOf<String>()
    val listMalls: MutableList<String> = mutableListOf<String>()
    val listBrands: MutableList<String> = mutableListOf<String>()
    lateinit var spinnerCity: Spinner
    lateinit var spinnerMall: Spinner
    lateinit var spinnerBrand: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(applicationContext, "", duration)

        //Постараемся заполнить список магазинов
        val response = shops.fillShops()
        if (response != ""){
            toast.setText(response)
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }
        //Распарсим даже если пустой
        cityMallBrand.fillListOfCityMallBrand(shops.listofShop)

        //Постараемся заполнить список городов
        listCitys.clear()
        if (!cityMallBrand.listOfCityMallBrand.isEmpty()) {
            listCitys.addAll(cityMallBrand.listOfCityMallBrand.keys.toMutableList().sorted() as MutableList<String>)
        }

        //Определим спинеры
        spinnerCity = findViewById(R.id.spin_City)
        spinnerMall = findViewById(R.id.spin_Mall)
        spinnerBrand = findViewById(R.id.spin_Brand)

        //Сделаем первый адаптер для спиннера городов
        val aaCity = ArrayAdapter(this, android.R.layout.simple_spinner_item, listCitys)
        aaCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCity.adapter = aaCity

        //Постараемся отобрать список торговых центорв из выбранного города
        //Так же сразу сгенерируем адаптер для спиннера торговых центров
        val selectedItemSpinnerCity: String = spinnerCity.selectedItem?.toString() ?: ""
        listMalls.clear()
        if (!cityMallBrand.listOfCityMallBrand.isEmpty()) {
            listMalls.addAll(
                cityMallBrand.listOfCityMallBrand[selectedItemSpinnerCity]?.keys?.toMutableList()
                    ?.sorted() as MutableList<String> ?: mutableListOf<String>()
            )
        }
        val aaMall = ArrayAdapter(this, android.R.layout.simple_spinner_item, listMalls)
        aaMall.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMall.adapter = aaMall

        //Постараемся заполнить список брендов и сделаем третий адаптер для брендов
        val selectedItepSpinnerMall: String = spinnerMall.selectedItem?.toString() ?: ""
        listBrands.clear()
        if (!cityMallBrand.listOfCityMallBrand.isEmpty()) {
            listBrands.addAll(
                cityMallBrand.listOfCityMallBrand.getValue(selectedItemSpinnerCity)
                    .getValue(selectedItepSpinnerMall).toMutableList()
                    .sorted() as MutableList<String>
            )
        }
        val aaBrand = ArrayAdapter(this, android.R.layout.simple_spinner_item, listBrands)
        aaBrand.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBrand.adapter = aaBrand

        //переопределим процедуры спиннеров по которым мы будем обновлять списки
        //Списки обновляются в зависимости от выбранного значения
        //По скольку это лист, то достаточно изменить лист и сказать адаптеру что требуется обновить данные
        spinnerCity.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){
                changelistMalls(parent.getItemAtPosition(position).toString())
                aaMall.notifyDataSetChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>){}
        }

        spinnerMall.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){
                changelistBrands(spinnerCity.selectedItem.toString(), parent.getItemAtPosition(position).toString())
                aaBrand.notifyDataSetChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>){}
        }

        spinnerBrand.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent:AdapterView<*>, view: View, position: Int, id: Long){}
            override fun onNothingSelected(parent: AdapterView<*>){}

        }

        //Кнопка синхронизировать. Сказано вывести сообщение пользователю, я и вывожу
        //Да в некоторых случаях можно получить нул нул нул.
        val btn_Sinc: Button = findViewById(R.id.btn_Sinch)
        btn_Sinc.setOnClickListener {
            toast.duration = Toast.LENGTH_LONG
            toast.setText("${spinnerCity.selectedItem.toString()} - ${spinnerMall.selectedItem.toString()} - ${spinnerBrand.selectedItem.toString()}")
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.show();
        }

        //Изврат честно. Не нашел лучше способа сделать.
        //Суть в том, что по скольку реализация идёт на спиннерах и на связанных списках то получается
        //Что приходится обновлять списки вручную и обновлять адаптеры иначе данные не подставляются
        val btn_FindById: Button = findViewById(R.id.btn_InputId)
        btn_FindById.setOnClickListener {
            val li: LayoutInflater = LayoutInflater.from(this);
            val promptsView: View = li.inflate(R.layout.prompt, null);
            val mDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            mDialogBuilder.setView(promptsView);
            val userInput: EditText = promptsView.findViewById(R.id.input_text);
            var shop: Shop = Shop()
            mDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener(
                        fun(dialog: DialogInterface, id: Int) {
                            if (userInput.text.isNotEmpty()) {
                                //Найдем магазин. Ну попробуем
                                shop = shops.findShopById(userInput.text.toString())
                                if (shop.id != "") {
                                    val cityName = shop.mall.city.name
                                    val mallName = shop.mall.name
                                    val brandName = shop.brand.name
                                    spinnerCity.setSelection(aaCity.getPosition(cityName))
                                    changelistMalls(cityName)
                                    aaMall.notifyDataSetChanged()
                                    spinnerMall.setSelection(aaMall.getPosition(mallName))
                                    changelistBrands(cityName, mallName)
                                    aaBrand.notifyDataSetChanged()
                                    spinnerBrand.setSelection(aaBrand.getPosition(brandName))
                                }
                            }
                        }
                    ))
                .setNegativeButton("Отмена",
                    DialogInterface.OnClickListener(
                        fun (dialog: DialogInterface, id: Int) {
                            dialog.cancel();
                        }
                    ));
            val alertDialog: AlertDialog = mDialogBuilder.create();
            alertDialog.show();

        }

    }

    //Вынес в функции чтобы меньше повторять код
    fun changelistMalls(cityString: String){
        listMalls.clear()
        listMalls.addAll(cityMallBrand.listOfCityMallBrand[cityString]?.keys?.toMutableList()?.sorted() as MutableList<String> ?: mutableListOf())
    }

    fun changelistBrands(cityString: String, mallString: String){
        listBrands.clear()
        listBrands.addAll(cityMallBrand.listOfCityMallBrand.getValue(cityString).getValue(mallString).toMutableList()?.sorted() as MutableList<String>)
    }
}